package com.inventiapp.stocktrack.subscriptions.application.internal;

import com.inventiapp.stocktrack.subscriptions.domain.model.entities.Plan;
import com.inventiapp.stocktrack.subscriptions.domain.repositories.PlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Slf4j
@Component
public class PlanInitializer {

    private final PlanRepository planRepository;

    public PlanInitializer(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedPlans() {
        var existingPlans = planRepository.findAll();
        if (existingPlans.isEmpty()) {
            log.info("Seeding subscription plans...");

            Plan starter = Plan.builder()
                    .name("Starter")
                    .description("Ideal para pequeños negocios y restaurantes. Controla tus insumos básicos de recetas, kits y balanzas inteligentes.")
                    .price(new BigDecimal("19.00"))
                    .currency("usd")
                    .billingInterval("month")
                    .stripePriceId("price_1TrBShB9ch5512t17XzsdLgK")
                    .maxRecipes(10)
                    .maxKits(5)
                    .maxDevices(2)
                    .build();
            starter.assignOwnerId(1L);

            Plan pro = Plan.builder()
                    .name("Pro")
                    .description("Perfecto para negocios en crecimiento. Gestión avanzada de inventario, recetas complejas, kits de producción y balanzas en tiempo real.")
                    .price(new BigDecimal("49.00"))
                    .currency("usd")
                    .billingInterval("month")
                    .stripePriceId("price_1TrBXYB9ch5512t108ds3MiJ")
                    .maxRecipes(50)
                    .maxKits(25)
                    .maxDevices(5)
                    .build();
            pro.assignOwnerId(1L);

            Plan enterprise = Plan.builder()
                    .name("Enterprise")
                    .description("Acceso ilimitado y soporte prioritario. Diseñado para grandes almacenes, múltiples sedes y operaciones a gran escala.")
                    .price(new BigDecimal("99.00"))
                    .currency("usd")
                    .billingInterval("month")
                    .stripePriceId("price_1TrBXqB9ch5512t1UW02eD5u")
                    .maxRecipes(-1)
                    .maxKits(-1)
                    .maxDevices(-1)
                    .build();
            enterprise.assignOwnerId(1L);

            planRepository.save(starter);
            planRepository.save(pro);
            planRepository.save(enterprise);
            log.info("Plans seeded successfully.");
        } else {
            log.info("Updating existing subscription plans with tailored descriptions...");
            for (Plan plan : existingPlans) {
                if (plan.getName().equalsIgnoreCase("Starter")) {
                    plan.setDescription("Ideal para pequeños negocios y restaurantes. Controla tus insumos básicos de recetas, kits y balanzas inteligentes.");
                } else if (plan.getName().equalsIgnoreCase("Pro")) {
                    plan.setDescription("Perfecto para negocios en crecimiento. Gestión avanzada de inventario, recetas complejas, kits de producción y balanzas en tiempo real.");
                } else if (plan.getName().equalsIgnoreCase("Enterprise")) {
                    plan.setDescription("Acceso ilimitado y soporte prioritario. Diseñado para grandes almacenes, múltiples sedes y operaciones a gran escala.");
                }
                planRepository.save(plan);
            }
            log.info("Plans updated successfully.");
        }
    }
}
