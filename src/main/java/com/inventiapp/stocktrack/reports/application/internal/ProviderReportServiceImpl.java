package com.inventiapp.stocktrack.reports.application.internal;

import com.inventiapp.stocktrack.iam.interfaces.acl.AuthenticatedUserContextFacade;
import com.inventiapp.stocktrack.inventory.domain.model.queries.GetAllProvidersIncludingDeletedQuery;
import com.inventiapp.stocktrack.inventory.domain.services.ProviderQueryService;
import com.inventiapp.stocktrack.reports.application.ProviderReportService;
import com.inventiapp.stocktrack.reports.interfaces.rest.resources.ProviderReportResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderReportServiceImpl implements ProviderReportService {

    private final ProviderQueryService providerQueryService;
    private final AuthenticatedUserContextFacade authenticatedUserContextFacade;

    public ProviderReportServiceImpl(ProviderQueryService providerQueryService, AuthenticatedUserContextFacade authenticatedUserContextFacade) {
        this.providerQueryService = providerQueryService;
        this.authenticatedUserContextFacade = authenticatedUserContextFacade;
    }

    @Override
    public List<ProviderReportResource> getProviderReport() {
        Long ownerId = authenticatedUserContextFacade.getCurrentOwnerId();

        return providerQueryService
                .handle(new GetAllProvidersIncludingDeletedQuery(ownerId))
                .stream()
                .map(p -> new ProviderReportResource(
                        p.getId(),
                        p.getFirstName(),
                        p.getLastName(),
                        p.getFullName(),
                        p.getEmail() != null ? p.getEmail().address() : null,       // Value Object
                        p.getPhoneNumber() != null ? p.getPhoneNumber().number() : null, // Value Object
                        p.getRuc() != null ? p.getRuc().value() : null,             // Value Object
                        p.getIsDeleted()
                ))
                .toList();
    }
}
