package com.inventiapp.stocktrack.subscriptions.domain.services;

import com.inventiapp.stocktrack.subscriptions.domain.model.aggregates.Subscription;
import com.inventiapp.stocktrack.subscriptions.domain.model.queries.GetSubscriptionByAccountIdQuery;
import com.inventiapp.stocktrack.subscriptions.interfaces.rest.resources.InvoiceResource;
import java.util.List;
import java.util.Optional;

public interface SubscriptionQueryService {
    Optional<Subscription> handle(GetSubscriptionByAccountIdQuery query);
    List<InvoiceResource> getInvoicesByAccountId(Long ownerId);
}
