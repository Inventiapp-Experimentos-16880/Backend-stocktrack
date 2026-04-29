package com.inventiapp.stocktrack.iam.domain.model.commands;

import java.util.List;

/**
 * Command for creating a user from staff management.
 * Users created by admin always get ROLE_USER.
 * Permissions control access to specific modules.
 * @param email The email for the new user
 * @param password The password for the new user
 * @param permissions List of permission names for module access
 * @param ownerId The owner ID (admin ID) who is creating this user. For multi-tenant isolation.
 */
public record CreateUserCommand(String email, String password, List<String> permissions, Long ownerId) {

    public CreateUserCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password cannot be null or blank");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId cannot be null or non-positive");
        }
    }
}

