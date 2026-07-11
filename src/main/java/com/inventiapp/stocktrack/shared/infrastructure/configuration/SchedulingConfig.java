package com.inventiapp.stocktrack.shared.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables Spring's scheduled task execution.
 * <p>
 * Kept separate from the application entry point so scheduling can be toggled/overridden
 * independently (e.g. disabled in tests via component scanning boundaries).
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
