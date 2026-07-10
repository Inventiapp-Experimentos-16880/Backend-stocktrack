package com.inventiapp.stocktrack.inventory.application.internal.commandservices;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.StockMovement;
import com.inventiapp.stocktrack.inventory.domain.model.commands.RecordStockMovementCommand;
import com.inventiapp.stocktrack.inventory.domain.services.StockMovementCommandService;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.StockMovementRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * Implementation of StockMovementCommandService.
 * Persists inventory movements (ENTRADA/SALIDA) as an additive audit trail.
 */
@Service
public class StockMovementCommandServiceImpl implements StockMovementCommandService {

    private final StockMovementRepository stockMovementRepository;

    public StockMovementCommandServiceImpl(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    @Override
    public Long handle(RecordStockMovementCommand command) {
        StockMovement movement = new StockMovement(command);
        try {
            StockMovement saved = stockMovementRepository.save(movement);
            return saved.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Error saving stock movement: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }
}
