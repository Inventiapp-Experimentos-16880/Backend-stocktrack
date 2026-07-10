package com.inventiapp.stocktrack.subscriptions.application.internal.queryservices;

import com.inventiapp.stocktrack.subscriptions.domain.model.entities.Plan;
import com.inventiapp.stocktrack.subscriptions.domain.model.queries.GetPlansQuery;
import com.inventiapp.stocktrack.subscriptions.domain.repositories.PlanRepository;
import com.inventiapp.stocktrack.subscriptions.domain.services.PlanQueryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlanQueryServiceImpl implements PlanQueryService {

    private final PlanRepository planRepository;

    public PlanQueryServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public List<Plan> handle(GetPlansQuery query) {
        return planRepository.findAll();
    }
}
