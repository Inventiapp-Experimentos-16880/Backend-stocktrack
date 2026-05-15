package com.inventiapp.stocktrack.inventory.application.internal.commandservices;

import com.inventiapp.stocktrack.inventory.domain.exceptions.CategoryNotFoundException;
import com.inventiapp.stocktrack.inventory.domain.exceptions.ProductAlreadyExistsException;
import com.inventiapp.stocktrack.inventory.domain.exceptions.ProductHasStockException;
import com.inventiapp.stocktrack.inventory.domain.exceptions.ProductNotFoundException;
import com.inventiapp.stocktrack.inventory.domain.exceptions.ProviderRequestException;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Product;
import com.inventiapp.stocktrack.inventory.domain.model.commands.CreateProductCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.DeleteProductCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.UpdateProductCommand;
import com.inventiapp.stocktrack.inventory.domain.model.events.ProductCreatedEvent;
import com.inventiapp.stocktrack.inventory.domain.model.events.ProductDeletedEvent;
import com.inventiapp.stocktrack.inventory.domain.model.events.ProductUpdatedEvent;
import com.inventiapp.stocktrack.inventory.domain.services.ProductCommandService;
import com.inventiapp.stocktrack.inventory.infrastructure.internal.CategoryRepository;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.BatchRepository;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.ProductRepository;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.ProviderRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of ProductCommandService.
 * @summary
 * Performs domain operations for Product aggregate: create, update and delete.
 * Exceptions from persistence layer are translated into domain-friendly exceptions.
 * @since 1.0
 */
@Service
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProviderRepository providerRepository;
    private final BatchRepository batchRepository;

    public ProductCommandServiceImpl(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProviderRepository providerRepository,
            BatchRepository batchRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.providerRepository = providerRepository;
        this.batchRepository = batchRepository;
    }

    /**
     * Handles the creation of a product.
     * Validates that no product with the same name and provider exists.
     * Registers a ProductCreatedEvent on the aggregate.
     *
     * @param command CreateProductCommand with product data
     * @return generated product id
     * @throws ProductAlreadyExistsException if a product with the same name and provider already exists
     */
    @Override
    public Long handle(CreateProductCommand command) {

        Long categoryId = Long.parseLong(command.categoryId());
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }

        Long providerId = Long.parseLong(command.providerId());
        if (!providerRepository.existsByIdAndOwnerIdIncludingDeleted(providerId, command.ownerId())) {
            throw new ProviderRequestException("Provider with ID " + providerId + " not found in the system");
        }

        if (productRepository.existsByNameAndProviderIdAndOwnerId(command.name(), command.providerId(), command.ownerId())) {
            throw new ProductAlreadyExistsException(command.name());
        }

        Product product = new Product(command);

        product.addDomainEvent(new ProductCreatedEvent(
                product,
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategoryId(),
                product.getProviderId(),
                product.getMinStock(),
                product.getUnitPrice(),
                product.getIsActive()
        ));

        try {
            Product saved = productRepository.save(product);
            return saved.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Error saving product: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }

    /**
     * Handles updating a product.
     * The aggregate's updateProduct(...) method updates the product information.
     * Registers a ProductUpdatedEvent on the aggregate.
     *
     * @param command UpdateProductCommand containing product id and updated values
     * @return Optional with updated product if exists
     * @throws ProductNotFoundException if the product does not exist
     */
    @Override
    public Optional<Product> handle(UpdateProductCommand command) {
        Product product = productRepository.findByIdAndOwnerId(command.productId(), command.ownerId())
                .orElseThrow(() -> new ProductNotFoundException(command.productId()));

        Long categoryId = Long.parseLong(command.categoryId());
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }

        Long providerId = Long.parseLong(command.providerId());
        if (!providerRepository.existsByIdAndOwnerIdIncludingDeleted(providerId, command.ownerId())) {
            throw new ProviderRequestException("Provider with ID " + providerId + " not found in the system");
        }

        product.updateProduct(command);

        product.addDomainEvent(new ProductUpdatedEvent(
                product,
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategoryId(),
                product.getProviderId(),
                product.getMinStock(),
                product.getUnitPrice(),
                product.getIsActive()
        ));

        try {
            return Optional.of(productRepository.save(product));
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Error updating product: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }

    /**
     * Handles deletion of a product.
     * Validates that the product has no stock before marking as inactive (soft delete).
     * Registers a ProductDeletedEvent on the aggregate.
     *
     * @param command DeleteProductCommand containing product id
     * @throws ProductNotFoundException if the product does not exist
     * @throws ProductHasStockException if the product has existing stock
     */
    @Override
    public void handle(DeleteProductCommand command) {
        Product product = productRepository.findByIdAndOwnerId(command.productId(), command.ownerId())
                .orElseThrow(() -> new ProductNotFoundException(command.productId()));

        // Check if product has stock
        Integer totalStock = batchRepository.sumQuantityByProductIdAndOwnerId(command.productId(), command.ownerId());
        if (totalStock > 0) {
            throw new ProductHasStockException("Cannot delete product with existing stock. Current stock: " + totalStock);
        }

        product.markAsInactive();

        product.addDomainEvent(new ProductDeletedEvent(product, product.getId()));

        try {
            productRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Error updating product: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }
}