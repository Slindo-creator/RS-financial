package com.afrofuturists.rsfinancial.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Maps to claim_status_history (V6 migration). Rows are never updated,
 * only inserted - each one is a permanent record of "the claim moved to
 * X at this time, for this reason", which is what lets a client or
 * adviser see the whole journey, not just the current state.
 */
@Entity
@Table(name = "claim_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimStatusHistory {

    @Id
    @Column(updatable = false, nullable = false)
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "claim_id", nullable = false)
    private UUID claimId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    @Column
    private String note;

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false)
    private Instant changedAt;
}
