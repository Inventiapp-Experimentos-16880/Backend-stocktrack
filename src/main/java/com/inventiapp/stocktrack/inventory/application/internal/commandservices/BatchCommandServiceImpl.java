package com.inventiapp.stocktrack.inventory.application.internal.commandservices;

import com.inventiapp.stocktrack.inventory.domain.exceptions.BatchNotFoundException;
import com.inventiapp.stocktrack.inventory.domain.exceptions.ProductNotFoundException;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Batch;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Product;
import com.inventiapp.stocktrack.inventory.domain.model.commands.CreateBatchCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.DeleteBatchCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RecordStockMovementCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.UpdateBatchCommand;
import com.inventiapp.stocktrack.inventory.domain.model.events.BatchCreatedEvent;
import com.inventiapp.stocktrack.inventory.domain.model.events.BatchDeletedEvent;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.MovementType;
import com.inventiapp.stocktrack.inventory.domain.services.BatchCommandService;
import com.inventiapp.stocktrack.inventory.domain.services.StockMovementCommandService;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.BatchRepository;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * Implementation of BatchCommandService.
 *
 * @summary Performs domain operations for Batch aggregate: create and delete.
 * Exceptions from persistence layer are translated into domain-friendly exceptions.
 * @since 1.0
 */
@Service
public class BatchCommandServiceImpl implements BatchCommandService {

    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final StockMovementCommandService stockMovementCommandService;

    public BatchCommandServiceImpl(BatchRepository batchRepository,
                                   ProductRepository productRepository,
                                   StockMovementCommandService stockMovementCommandService) {
        this.batchRepository = batchRepository;
        this.productRepository = productRepository;
        this.stockMovementCommandService = stockMovementCommandService;
    }

    /**
     * Handles the creation of a batch.
     * Validates that the product exists and reactivates it if inactive.
     * Registers a BatchCreatedEvent on the aggregate.
     *
     * @param command CreateBatchCommand with batch data
     * @return generated batch id
     * @throws ProductNotFoundException if the product does not exist
     */
    @Override
    public Long handle(CreateBatchCommand command) {
        Product product = productRepository.findByIdAndOwnerId(command.productId(), command.ownerId())
                .orElseThrow(() -> new ProductNotFoundException(command.productId()));

        // Reactivate product if inactive
        if (!product.getIsActive()) {
            product.markAsActive();
            productRepository.save(product);
        }

        Batch batch = new Batch(command);

        batch.addDomainEvent(new BatchCreatedEvent(
                batch,
                batch.getId(),
                batch.getProductId(),
                batch.getQuantity(),
                batch.getExpirationDate(),
                batch.getReceptionDate()
        ));

        try {
            Batch saved = batchRepository.save(batch);

            // Record the initial ENTRADA movement for the batch history.
            if (saved.getQuantity() != null && saved.getQuantity() > 0) {
                stockMovementCommandService.handle(new RecordStockMovementCommand(
                        saved.getProductId(),
                        saved.getId(),
                        MovementType.ENTRADA,
                        saved.getQuantity(),
                        saved.getReceptionDate(),
                        saved.getOwnerId()
                ));
            }

            return saved.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Error saving batch: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }

    /**
     * Handles deletion of a batch.
     * Registers a BatchDeletedEvent on the aggregate before deleting from repository.
     *
     * @param command DeleteBatchCommand containing batch id
     * @throws BatchNotFoundException if the batch does not exist
     */
    @Override
    public void handle(DeleteBatchCommand command) {
        Batch batch = batchRepository.findByIdAndOwnerId(command.batchId(), command.ownerId())
                .orElseThrow(() -> new BatchNotFoundException(command.batchId()));

        batch.addDomainEvent(new BatchDeletedEvent(batch, batch.getId()));

        try {
            batchRepository.delete(batch);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Error deleting batch: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }

    @Override
    public Optional<Batch> handle(UpdateBatchCommand command) {
        Batch batch = batchRepository.findByIdAndOwnerId(command.batchId(), command.ownerId())
                .orElseThrow(() -> new BatchNotFoundException(command.batchId()));

        if (!productRepository.existsById(batch.getProductId())) {
            throw new ProductNotFoundException(batch.getProductId());
        }

        int previousQuantity = batch.getQuantity();

        if (command.newQuantity() >= 0) {
            batch.setQuantity(command.newQuantity());
        } else {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        try {
            Batch updated = batchRepository.save(batch);

            // Record the movement implied by the quantity delta: a decrease is a SALIDA
            // (e.g. a sale routed through the ACL), an increase is an ENTRADA (restock).
            int delta = updated.getQuantity() - previousQuantity;
            if (delta != 0) {
                stockMovementCommandService.handle(new RecordStockMovementCommand(
                        updated.getProductId(),
                        updated.getId(),
                        delta > 0 ? MovementType.ENTRADA : MovementType.SALIDA,
                        Math.abs(delta),
                        new Date(),
                        updated.getOwnerId()
                ));
            }

            return Optional.of(updated);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Error updating product: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }
}