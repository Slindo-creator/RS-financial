package com.afrofuturists.rsfinancial.repository;

import com.afrofuturists.rsfinancial.domain.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, UUID> {

    List<ServiceRequest> findByAdviserId(UUID adviserId);

    List<ServiceRequest> findByClientId(UUID clientId);
}
