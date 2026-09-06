package com.afrofuturists.rsfinancial.dto;

import com.afrofuturists.rsfinancial.domain.ClaimStatus;
import com.afrofuturists.rsfinancial.domain.VehicleUse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ClaimDtos {

    // What "tap Report an Accident or Loss" returns immediately - fixed
    // reference content, not something tied to any one claim, so it's
    // never persisted or client-specific. See ClaimService
    // .getAccidentChecklist for where the actual list lives.
    public record AccidentChecklistResponse(
            List<String> gatherAtTheScene,
            String policeReportingReminder
    ) {}

    public record RegisterClaimRequest(
            @NotNull UUID clientId,
            @NotBlank String insurerName,
            @NotNull Instant incidentAt,
            String description,
            boolean policeNotified,
            String policeCaseNumber,
            String witnessName,
            String witnessContact,
            boolean witnessStatementTaken,
            String driverName,
            VehicleUse vehicleUse,
            String otherPartyDetails
    ) {}

    public record UpdateClaimStatusRequest(
            @NotNull ClaimStatus status,
            String note,
            // Only meaningful when status is moving to CLAIM_NUMBER_ISSUED -
            // harmless to send null/omit otherwise.
            String claimNumber,
            String claimHandlerName
    ) {}

    public record ClaimStatusHistoryEntry(
            ClaimStatus status,
            String note,
            Instant changedAt
    ) {}

    public record ClaimResponse(
            UUID id,
            UUID clientId,
            String insurerName,
            Instant incidentAt,
            String description,
            boolean policeNotified,
            String policeCaseNumber,
            String witnessName,
            String witnessContact,
            boolean witnessStatementTaken,
            String driverName,
            VehicleUse vehicleUse,
            String otherPartyDetails,
            ClaimStatus status,
            String claimNumber,
            String claimHandlerName,
            List<ClaimStatusHistoryEntry> history
    ) {}
}
