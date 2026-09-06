package com.afrofuturists.rsfinancial.dto;

import com.afrofuturists.rsfinancial.domain.ServiceRequestStatus;
import com.afrofuturists.rsfinancial.domain.ServiceRequestType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class ServiceRequestDtos {

    public record CreateServiceRequestRequest(
            @NotNull UUID clientId,
            @NotNull ServiceRequestType requestType,
            // Freeform - e.g. for CHANGE_OF_ADDRESS this might be a JSON
            // string the frontend builds from its own address form.
            // This layer doesn't validate its shape; see the migration's
            // note on why that's a deliberate choice, not an oversight.
            String details
    ) {}

    public record UpdateStatusRequest(
            @NotNull ServiceRequestStatus status,
            String resolutionNotes
    ) {}

    public record ServiceRequestResponse(
            UUID id,
            UUID clientId,
            ServiceRequestType requestType,
            String details,
            ServiceRequestStatus status,
            String resolutionNotes,
            Instant createdAt,
            Instant completedAt
    ) {}
}
