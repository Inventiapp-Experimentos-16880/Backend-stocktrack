package com.inventiapp.stocktrack.subscriptions.domain.services;

import com.inventiapp.stocktrack.subscriptions.domain.model.entities.Plan;
import com.inventiapp.stocktrack.subscriptions.domain.model.queries.GetPlansQuery;
import java.util.List;

public interface PlanQueryService {
    List<Plan> handle(GetPlansQuery query);
}
