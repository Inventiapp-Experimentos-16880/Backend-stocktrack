package com.inventiapp.stocktrack.iam.interfaces.acl;

import com.inventiapp.stocktrack.iam.application.internal.outboundservices.tokens.TokenService;
import com.inventiapp.stocktrack.iam.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * AuthenticatedUserContextFacade
 *
 * Provides access to the authenticated user's context information extracted from JWT.
 * Used by Controllers to obtain ownerId and other user claims for multi-tenant isolation.
 */
@Service
public class AuthenticatedUserContextFacade {

    private final BearerTokenService tokenService;
    private final HttpServletRequest request;

    public AuthenticatedUserContextFacade(BearerTokenService tokenService, HttpServletRequest request) {
        this.tokenService = tokenService;
        this.request = request;
    }

    /**
     * Get the authenticated user's ID from the JWT token
     * @return The user ID from the token subject, or null if not authenticated
     */
    public Long getCurrentUserId() {
        String token = getTokenFromRequest();
        if (token == null || !tokenService.validateToken(token)) {
            return null;
        }
        return tokenService.getUserIdFromToken(token);
    }

    /**
     * Get the authenticated user's owner ID from the JWT token.
     * This is the key for multi-tenant logical isolation.
     * For admins: ownerId == userId
     * For workers: ownerId == admin's userId
     *
     * @return The owner ID from token claims, or null if not authenticated
     */
    public Long getCurrentOwnerId() {
        String token = getTokenFromRequest();
        if (token == null || !tokenService.validateToken(token)) {
            return null;
        }
        return tokenService.getOwnerIdFromToken(token);
    }

    /**
     * Get the authenticated user's email from the JWT token
     * @return The email from token claims, or null if not authenticated
     */
    public String getCurrentUserEmail() {
        String token = getTokenFromRequest();
        if (token == null || !tokenService.validateToken(token)) {
            return null;
        }
        return tokenService.getEmailFromToken(token);
    }

    /**
     * Extract the JWT token string from the HTTP request Authorization header
     * @return The JWT token or null if not present
     */
    private String getTokenFromRequest() {
        return tokenService.getBearerTokenFrom(request);
    }

    /**
     * Validate that the current user's ownerId matches the provided ownerId.
     * Prevents users from accessing data of other owners.
     *
     * @param ownerId The ownerId to validate
     * @return true if the current user's ownerId matches the provided ownerId
     */
    public boolean validateOwnership(Long ownerId) {
        if (ownerId == null) {
            return false;
        }
        Long currentOwnerId = getCurrentOwnerId();
        return currentOwnerId != null && currentOwnerId.equals(ownerId);
    }
}

