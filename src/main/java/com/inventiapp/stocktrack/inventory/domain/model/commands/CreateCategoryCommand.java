package com.inventiapp.stocktrack.inventory.domain.model.commands;

/**
 * CreateCategoryCommand
 * @summary
 * CreateCategoryCommand is a record class that represents the command to create a category.
 * @param name The category name. Cannot be null or empty.
 * @param description The category description. Can be null or empty.
 * @param ownerId The owner ID for multi-tenant isolation. Cannot be null or non-positive.
 * @since 1.0
 */
public record CreateCategoryCommand(String name, String description, Long ownerId) {
    /**
     * Validates the command.
     * @throws IllegalArgumentException If name is null or empty, or if ownerId is invalid
     */
    public CreateCategoryCommand {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Category name cannot be null or empty");
        if (ownerId == null || ownerId <= 0)
            throw new IllegalArgumentException("ownerId cannot be null or non-positive");
    }
}


