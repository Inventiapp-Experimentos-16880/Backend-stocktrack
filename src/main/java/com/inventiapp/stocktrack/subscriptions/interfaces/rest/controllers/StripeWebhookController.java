package com.inventiapp.stocktrack.subscriptions.interfaces.rest.controllers;

import com.inventiapp.stocktrack.subscriptions.domain.services.SubscriptionCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/subscriptions/webhooks")
@Tag(name = "Subscriptions", description = "Subscription management endpoints")
public class StripeWebhookController {

    private final SubscriptionCommandService subscriptionCommandService;

    public StripeWebhookController(SubscriptionCommandService subscriptionCommandService) {
        this.subscriptionCommandService = subscriptionCommandService;
    }

    @Operation(summary = "Handle Stripe posted events")
    @PostMapping("/stripe")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        log.info("Received Stripe Webhook call");
        try {
            subscriptionCommandService.handleStripeWebhook(payload, sigHeader);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            log.error("Error processing Stripe webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook handling error: " + e.getMessage());
        }
    }
}
