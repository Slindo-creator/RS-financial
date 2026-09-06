package com.afrofuturists.rsfinancial.service;

import com.afrofuturists.rsfinancial.domain.Client;
import com.afrofuturists.rsfinancial.domain.ServiceRequest;
import com.afrofuturists.rsfinancial.domain.ServiceRequestStatus;
import com.afrofuturists.rsfinancial.dto.ServiceRequestDtos.CreateServiceRequestRequest;
import com.afrofuturists.rsfinancial.dto.ServiceRequestDtos.ServiceRequestResponse;
import com.afrofuturists.rsfinancial.repository.ClientRepository;
import com.afrofuturists.rsfinancial.repository.ServiceRequestRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final ClientRepository clientRepository;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                 ClientRepository clientRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public ServiceRequestResponse createRequest(CreateServiceRequestRequest request, UUID adviserId) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new NoSuchElementException("Client not found: " + request.clientId()));
        if (!client.getAdviserId().equals(adviserId)) {
            throw new AccessDeniedException("This client is not assigned to you");
        }

        ServiceRequest serviceRequest = ServiceRequest.builder()
                .clientId(request.clientId())
                .adviserId(adviserId)
                .requestType(request.requestType())
                .details(request.details())
                .build();

        return toResponse(serviceRequestRepository.save(serviceRequest));
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> getRequestsForAdviser(UUID adviserId) {
        return serviceRequestRepository.findByAdviserId(adviserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> getRequestsForClient(UUID clientId, UUID adviserId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client not found"));
        if (!client.getAdviserId().equals(adviserId)) {
            throw new AccessDeniedException("This client is not assigned to you");
        }

        return serviceRequestRepository.findByClientId(clientId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ServiceRequestResponse updateStatus(UUID requestId, UUID adviserId,
                                                ServiceRequestStatus newStatus, String resolutionNotes) {
        ServiceRequest serviceRequest = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Service request not found"));
        if (!serviceRequest.getAdviserId().equals(adviserId)) {
            throw new AccessDeniedException("This request is not yours to update");
        }

        serviceRequest.setStatus(newStatus);
        serviceRequest.setResolutionNotes(resolutionNotes);
        if (newStatus == ServiceRequestStatus.COMPLETED || newStatus == ServiceRequestStatus.REJECTED) {
            serviceRequest.setCompletedAt(Instant.now());
        }

        return toResponse(serviceRequestRepository.save(serviceRequest));
    }

    private ServiceRequestResponse toResponse(ServiceRequest serviceRequest) {
        return new ServiceRequestResponse(
                serviceRequest.getId(),
                serviceRequest.getClientId(),
                serviceRequest.getRequestType(),
                serviceRequest.getDetails(),
                serviceRequest.getStatus(),
                serviceRequest.getResolutionNotes(),
                serviceRequest.getCreatedAt(),
                serviceRequest.getCompletedAt()
        );
    }
}
