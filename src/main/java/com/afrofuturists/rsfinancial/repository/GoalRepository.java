package com.afrofuturists.rsfinancial.repository;

import com.afrofuturists.rsfinancial.domain.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {

    // Can't derive this one from the method name alone (Spring Data
    // doesn't know how to traverse an @ElementCollection by "contains"
    // in a findBy-style name), so it's an explicit JPQL query instead.
    @Query("SELECT g FROM Goal g JOIN g.clientIds c WHERE c = :clientId")
    List<Goal> findByClientId(@Param("clientId") UUID clientId);

    List<Goal> findByAdviserId(UUID adviserId);
}
