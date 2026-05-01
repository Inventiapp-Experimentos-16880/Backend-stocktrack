package com.inventiapp.stocktrack.inventory.interfaces.rest.transform;

import com.inventiapp.stocktrack.inventory.domain.model.commands.UpdateProviderCommand;
import com.inventiapp.stocktrack.inventory.interfaces.rest.resources.UpdateProviderResource;

/**
 * Assembler to convert a UpdateProviderResource to a UpdateProviderCommand.
 */
public class UpdateProviderCommandFromResourceAssembler {

    /**
     * Converts a UpdateProviderResource to a UpdateProviderCommand.
     *
     * @param providerId The provider ID.
     * @param resource   The {@link UpdateProviderResource} to convert.
     * @return The resulting {@link UpdateProviderCommand}.
     */
    public static UpdateProviderCommand toCommandFromResource(Long providerId, UpdateProviderResource resource, Long ownerId) {
        if (providerId == null || providerId <= 0) {
            throw new IllegalArgumentException("providerId must be a positive number");
        }
        if (resource == null) {
            throw new IllegalArgumentException("UpdateProviderResource cannot be null");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("ownerId must be a positive number");
        }

        return new UpdateProviderCommand(
                providerId,
                resource.firstName(),
                resource.lastName(),
                resource.phoneNumber(),
                resource.email(),
                resource.ruc(),
                ownerId
        );
    }
}
