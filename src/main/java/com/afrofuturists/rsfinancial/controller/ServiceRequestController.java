package com.afrofuturists.rsfinancial.controller;

import com.afrofuturists.rsfinancial.dto.ServiceRequestDtos.CreateServiceRequestRequest;
import com.afrofuturists.rsfinancial.dto.ServiceRequestDtos.ServiceRequestResponse;
import com.afrofuturists.rsfinancial.dto.ServiceRequestDtos.UpdateStatusRequest;
import com.afrofuturists.rsfinancial.security.StaffUserDetails;
import com.afrofuturists.rsfinancial.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @PostMapping
    public ResponseEntity<ServiceRequestResponse> createRequest(
            @Valid @RequestBody CreateServiceRequestRequest request,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(serviceRequestService.createRequest(request, principal.getId()));
    }

    @GetMapping
    public ResponseEntity<List<ServiceRequestResponse>> getMyRequests(
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(serviceRequestService.getRequestsForAdviser(principal.getId()));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ServiceRequestResponse>> getRequestsForClient(
            @PathVariable UUID clientId,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(serviceRequestService.getRequestsForClient(clientId, principal.getId()));
    }

    @PatchMapping("/{requestId}/status")
    public ResponseEntity<ServiceRequestResponse> updateStatus(
            @PathVariable UUID requestId,
            @Valid @RequestBody UpdateStatusRequest request,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        ServiceRequestResponse response = serviceRequestService.updateStatus(
                requestId, principal.getId(), request.status(), request.resolutionNotes());
        return ResponseEntity.ok(response);
    }
}
