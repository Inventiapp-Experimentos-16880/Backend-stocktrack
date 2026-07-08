package com.inventiapp.stocktrack.inventory.application.internal.queryservices;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.StockMovement;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetStockMovementsByBatchIdQuery;
import com.inventiapp.stocktrack.inventory.domain.services.StockMovementQueryService;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of StockMovementQueryService.
 * Provides read operations for the StockMovement aggregate.
 */
@Service
@Transactional(readOnly = true)
public class StockMovementQueryServiceImpl implements StockMovementQueryService {

    private final StockMovementRepository stockMovementRepository;

    public StockMovementQueryServiceImpl(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    @Override
    public List<StockMovement> handle(GetStockMovementsByBatchIdQuery query) {
        return stockMovementRepository.findByBatchIdAndOwnerIdOrderByOccurredAtAsc(query.batchId(), query.ownerId());
    }
}
