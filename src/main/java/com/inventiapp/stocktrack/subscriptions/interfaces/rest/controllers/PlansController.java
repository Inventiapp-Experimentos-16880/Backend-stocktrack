package com.inventiapp.stocktrack.subscriptions.interfaces.rest.controllers;

import com.inventiapp.stocktrack.subscriptions.domain.model.queries.GetPlansQuery;
import com.inventiapp.stocktrack.subscriptions.domain.services.PlanQueryService;
import com.inventiapp.stocktrack.subscriptions.interfaces.rest.resources.PlanResource;
import com.inventiapp.stocktrack.subscriptions.interfaces.rest.transform.PlanResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/plans", produces = "application/json")
@Tag(name = "Subscriptions", description = "Subscription management endpoints")
public class PlansController {

    private final PlanQueryService planQueryService;

    public PlansController(PlanQueryService planQueryService) {
        this.planQueryService = planQueryService;
    }

    @Operation(summary = "List subscription plans")
    @GetMapping
    public ResponseEntity<List<PlanResource>> getPlans() {
        var query = new GetPlansQuery();
        var plans = planQueryService.handle(query);
        var resources = plans.stream()
                .map(PlanResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
