package com.inventiapp.stocktrack.inventory.infrastructure.persistence.jpa.repositories;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Provider aggregate.
 * All queries are automatically filtered by ownerId for multi-tenant isolation.
 */
@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    /**
     * Find a provider by ID within an owner's data
     * @param id provider ID
     * @param ownerId owner ID for multi-tenant isolation
     * @return Optional containing the provider if found
     */
    @Query("SELECT p FROM Provider p WHERE p.id = :id AND p.ownerId = :ownerId")
    Optional<Provider> findByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    /**
     * Find all providers for an owner
     * @param ownerId owner ID for multi-tenant isolation
     * @return List of providers
     */
    @Query("SELECT p FROM Provider p WHERE p.ownerId = :ownerId ORDER BY p.id")
    List<Provider> findAllByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * Check if provider exists by RUC for an owner
     * @param ruc provider RUC
     * @param ownerId owner ID for multi-tenant isolation
     * @return true if exists
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Provider p WHERE p.ruc.value = :ruc AND p.ownerId = :ownerId")
    boolean existsByRucAndOwnerId(@Param("ruc") String ruc, @Param("ownerId") Long ownerId);
}
