package com.inventiapp.stocktrack.inventory.interfaces.rest.transform;

import com.inventiapp.stocktrack.inventory.domain.model.commands.RegisterMitigationActionCommand;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.MitigationActionType;
import com.inventiapp.stocktrack.inventory.interfaces.rest.resources.RegisterMitigationActionResource;

/**
 * Assembler to build a RegisterMitigationActionCommand from the REST request resource.
 */
public class RegisterMitigationActionCommandFromResourceAssembler {

    /**
     * Converts the request resource into a domain command.
     *
     * @param alertId  alert id taken from the path
     * @param resource request payload (actionType, optional quantity)
     * @param ownerId  authenticated owner id
     * @return the register mitigation action command
     * @throws IllegalArgumentException if actionType is not a valid MitigationActionType
     */
    public static RegisterMitigationActionCommand toCommandFromResource(Long alertId,
                                                                        RegisterMitigationActionResource resource,
                                                                        Long ownerId) {
        MitigationActionType actionType = MitigationActionType.valueOf(resource.actionType().trim().toUpperCase());
        return new RegisterMitigationActionCommand(alertId, actionType, resource.quantity(), ownerId);
    }
}
