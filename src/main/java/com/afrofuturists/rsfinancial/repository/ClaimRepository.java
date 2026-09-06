package com.afrofuturists.rsfinancial.repository;

import com.afrofuturists.rsfinancial.domain.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID> {

    List<Claim> findByClientId(UUID clientId);

    List<Claim> findByAdviserId(UUID adviserId);
}
