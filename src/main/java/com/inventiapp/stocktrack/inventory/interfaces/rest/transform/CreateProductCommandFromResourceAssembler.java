package com.inventiapp.stocktrack.inventory.interfaces.rest.transform;

import com.inventiapp.stocktrack.inventory.domain.model.commands.CreateProductCommand;
import com.inventiapp.stocktrack.inventory.interfaces.rest.resources.CreateProductResource;

/**
 * Assembler that converts a CreateProductResource into a CreateProductCommand.
 */
public class CreateProductCommandFromResourceAssembler {

    private CreateProductCommandFromResourceAssembler() {}

    /**
     * Converts a CreateProductResource to a CreateProductCommand.
     *
     * @param resource the incoming resource from the API
     * @param ownerId current authenticated owner's id
     * @return the command ready to be handled by the application layer
     * @throws IllegalArgumentException if resource is null
     */
    public static CreateProductCommand toCommandFromResource(CreateProductResource resource, Long ownerId) {
        if (resource == null) {
            throw new IllegalArgumentException("CreateProductResource cannot be null");
        }

        return new CreateProductCommand(
                resource.name(),
                resource.description(),
                resource.categoryId(),
                resource.providerId(),
                resource.minStock(),
                resource.unitPrice(),
                resource.isActive(),
                ownerId
        );
    }
}
