package com.afrofuturists.rsfinancial.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Maps to claims (V6 migration). claimNumber and claimHandlerName start
 * null and get filled in once the insurer issues them (status moves to
 * CLAIM_NUMBER_ISSUED) - see ClaimService.updateStatus.
 */
@Entity
@Table(name = "claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @Column(updatable = false, nullable = false)
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "adviser_id", nullable = false)
    private UUID adviserId;

    @Column(name = "insurer_name", nullable = false)
    private String insurerName;

    @Column(name = "incident_at", nullable = false)
    private Instant incidentAt;

    @Column
    private String description;

    @Column(name = "police_notified", nullable = false)
    @Builder.Default
    private boolean policeNotified = false;

    @Column(name = "police_case_number")
    private String policeCaseNumber;

    @Column(name = "witness_name")
    private String witnessName;

    @Column(name = "witness_contact")
    private String witnessContact;

    @Column(name = "witness_statement_taken", nullable = false)
    @Builder.Default
    private boolean witnessStatementTaken = false;

    @Column(name = "driver_name")
    private String driverName;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_use")
    private VehicleUse vehicleUse;

    @Column(name = "other_party_details")
    private String otherPartyDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ClaimStatus status = ClaimStatus.SUBMITTED;

    @Column(name = "claim_number")
    private String claimNumber;

    @Column(name = "claim_handler_name")
    private String claimHandlerName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
