package com.afrofuturists.rsfinancial.domain;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maps to service_requests (V5 migration). Unlike Reminder, clientId is
 * NOT nullable here - every one of these task types in the problem
 * statement is inherently about a specific client (you can't request an
 * IRP5 or change a bank detail for "nobody in particular").
 */
@Entity
@Table(name = "service_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequest {

    @Id
    @Column(updatable = false, nullable = false)
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "adviser_id", nullable = false)
    private UUID adviserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private ServiceRequestType requestType;

    @Column
    private String details;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ServiceRequestStatus status = ServiceRequestStatus.PENDING;

    @Column(name = "resolution_notes")
    private String resolutionNotes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
