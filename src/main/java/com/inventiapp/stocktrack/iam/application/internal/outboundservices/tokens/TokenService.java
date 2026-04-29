package com.inventiapp.stocktrack.iam.application.internal.outboundservices.tokens;

import java.util.List;

/**
 * Service interface for token operations
 */
public interface TokenService {

    /**
     * Generate a token for a user with necessary claims
     * @param userId The user ID (used as subject in JWT)
     * @param email The user email
     * @param roles List of user roles
     * @return The generated token
     */
    String generateToken(Long userId, String email, List<String> roles);

    /**
     * Generate a token for a user with owner ID for multi-tenant isolation
     * @param userId The user ID (used as subject in JWT)
     * @param email The user email
     * @param roles List of user roles
     * @param ownerId The owner ID for multi-tenant logical isolation
     * @return The generated token
     */
    String generateToken(Long userId, String email, List<String> roles, Long ownerId);

    /**
     * Extract user ID from a token
     * @param token The token
     * @return The user ID (subject from JWT)
     */
    Long getUserIdFromToken(String token);

    /**
     * Extract email from a token
     * @param token The token
     * @return The email (from JWT claims)
     */
    String getEmailFromToken(String token);

    /**
     * Extract roles from a token
     * @param token The token
     * @return List of roles
     */
    List<String> getRolesFromToken(String token);

    /**
     * Extract owner ID from a token for multi-tenant isolation
     * @param token The token
     * @return The owner ID for the authenticated user
     */
    Long getOwnerIdFromToken(String token);

    /**
     * Validate a token
     * @param token The token to validate
     * @return true if valid, false otherwise
     */
    boolean validateToken(String token);
}

