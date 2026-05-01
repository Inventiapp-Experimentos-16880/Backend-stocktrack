package com.inventiapp.stocktrack.inventory.domain.model.commands;

/**
 * Command to create a provider.
 *
 * @param firstName   the provider first name. Cannot be null or blank.
 * @param lastName    the provider last name.  Cannot be null or blank.
 * @param phoneNumber the provider phone number. Cannot be null or blank.
 * @param email       the provider email. Cannot be null or blank.
 * @param ruc         the provider RUC (Peruvian tax id). Cannot be null or blank.
 * @param ownerId     the owner ID for multi-tenant isolation. Cannot be null or non-positive.
 * @throws IllegalArgumentException if any required parameter is null or blank.
 * @since 1.0
 */
public record CreateProviderCommand(
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String ruc,
        Long ownerId
) {
    public CreateProviderCommand {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("firstName cannot be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("lastName cannot be null or blank");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("phoneNumber cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email cannot be null or blank");
        }
        if (ruc == null || ruc.isBlank()) {
            throw new IllegalArgumentException("ruc cannot be null or blank");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId cannot be null or non-positive");
        }
    }
}
