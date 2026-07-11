package com.inventiapp.stocktrack.inventory.application.internal.queryservices;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.ExpirationAlert;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetAllExpirationAlertsQuery;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetExpirationAlertsByStatusQuery;
import com.inventiapp.stocktrack.inventory.domain.services.ExpirationAlertQueryService;
import com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories.ExpirationAlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of ExpirationAlertQueryService.
 * Provides read operations for the ExpirationAlert aggregate.
 */
@Service
@Transactional(readOnly = true)
public class ExpirationAlertQueryServiceImpl implements ExpirationAlertQueryService {

    private final ExpirationAlertRepository expirationAlertRepository;

    public ExpirationAlertQueryServiceImpl(ExpirationAlertRepository expirationAlertRepository) {
        this.expirationAlertRepository = expirationAlertRepository;
    }

    @Override
    public List<ExpirationAlert> handle(GetAllExpirationAlertsQuery query) {
        return expirationAlertRepository.findAllByOwnerId(query.ownerId());
    }

    @Override
    public List<ExpirationAlert> handle(GetExpirationAlertsByStatusQuery query) {
        return expirationAlertRepository.findAllByOwnerIdAndStatus(query.ownerId(), query.status());
    }
}
