package com.inventiapp.stocktrack.iam.infrastructure.persistence.jpa.repositories;

import com.inventiapp.stocktrack.iam.domain.model.aggregates.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository for User aggregate
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by email
     * @param email The email
     * @return Optional User
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists by email
     * @param email The email
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Update the owner_id for a user using native query (bypasses updatable=false constraint)
     * This is used during signup to set ownerId to the user's own ID after it is generated
     * @param userId The user ID to update
     * @param ownerId The owner ID to set
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE users SET owner_id = :ownerId WHERE id = :userId", nativeQuery = true)
    void updateOwnerIdNative(Long userId, Long ownerId);
}

