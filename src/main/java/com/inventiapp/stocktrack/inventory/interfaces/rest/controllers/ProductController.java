package com.inventiapp.stocktrack.inventory.interfaces.rest.controllers;

import com.inventiapp.stocktrack.inventory.domain.exceptions.ProductAlreadyExistsException;
import com.inventiapp.stocktrack.inventory.domain.exceptions.ProductNotFoundException;
import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Product;
import com.inventiapp.stocktrack.inventory.domain.model.commands.DeleteProductCommand;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetAllProductsQuery;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetProductByIdQuery;
import com.inventiapp.stocktrack.inventory.domain.services.ProductCommandService;
import com.inventiapp.stocktrack.inventory.domain.services.ProductQueryService;
import com.inventiapp.stocktrack.iam.interfaces.acl.AuthenticatedUserContextFacade;
import com.inventiapp.stocktrack.inventory.interfaces.rest.resources.CreateProductResource;
import com.inventiapp.stocktrack.inventory.interfaces.rest.resources.ProductResource;
import com.inventiapp.stocktrack.inventory.interfaces.rest.resources.UpdateProductResource;
import com.inventiapp.stocktrack.inventory.interfaces.rest.transform.CreateProductCommandFromResourceAssembler;
import com.inventiapp.stocktrack.inventory.interfaces.rest.transform.ProductResourceFromEntityAssembler;
import com.inventiapp.stocktrack.inventory.interfaces.rest.transform.UpdateProductCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/products", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Products", description = "Endpoints for products")
public class ProductController {

    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;
    private final AuthenticatedUserContextFacade authenticatedUserContextFacade;

    public ProductController(ProductCommandService productCommandService,
                             ProductQueryService productQueryService,
                             AuthenticatedUserContextFacade authenticatedUserContextFacade) {
        this.productCommandService = productCommandService;
        this.productQueryService = productQueryService;
        this.authenticatedUserContextFacade = authenticatedUserContextFacade;
    }

    @Operation(summary = "Create a product", description = "Creates a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResource> createProduct(@Valid @RequestBody CreateProductResource resource) {
        try {
            var ownerId = authenticatedUserContextFacade.getCurrentOwnerId();
            var command = CreateProductCommandFromResourceAssembler.toCommandFromResource(resource, ownerId);
            Long createdId = productCommandService.handle(command);

            var opt = productQueryService.handle(new GetProductByIdQuery(createdId, ownerId));
            return opt.map(product -> {
                        ProductResource response = ProductResourceFromEntityAssembler.toResource(product);
                        return ResponseEntity.created(URI.create("/api/v1/products/" + createdId)).body(response);
                    })
                    .orElseGet(() -> ResponseEntity.badRequest().build());

        } catch (IllegalArgumentException | ProductAlreadyExistsException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get product by id", description = "Fetch a product by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResource> getById(@PathVariable Long id) {
        try {
            var ownerId = authenticatedUserContextFacade.getCurrentOwnerId();
            Optional<Product> opt = productQueryService.handle(new GetProductByIdQuery(id, ownerId));
            return opt.map(ProductResourceFromEntityAssembler::toResource)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get all products", description = "Retrieve all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found")
    })
    @GetMapping
    public ResponseEntity<List<ProductResource>> getAll() {
        var ownerId = authenticatedUserContextFacade.getCurrentOwnerId();
        List<Product> products = productQueryService.handle(new GetAllProductsQuery(ownerId));
        List<ProductResource> resources = products.stream()
                .map(ProductResourceFromEntityAssembler::toResource)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Update a product", description = "Updates product data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResource> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateProductResource resource) {
        try {
            var ownerId = authenticatedUserContextFacade.getCurrentOwnerId();
            var command = UpdateProductCommandFromResourceAssembler.toCommandFromResource(id, resource, ownerId);
            Optional<Product> updated = productCommandService.handle(command);

            return updated
                    .map(ProductResourceFromEntityAssembler::toResource)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (ProductNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a product", description = "Deletes a product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            var ownerId = authenticatedUserContextFacade.getCurrentOwnerId();
            var command = new DeleteProductCommand(id, ownerId);
            productCommandService.handle(command);
            return ResponseEntity.noContent().build();
        } catch (ProductNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
