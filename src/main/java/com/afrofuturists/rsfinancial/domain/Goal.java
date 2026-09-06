package com.afrofuturists.rsfinancial.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Maps to goals + goal_clients (V3 migration). clientIds is a plain
 * Set<UUID> via @ElementCollection rather than a full @ManyToMany to
 * Client - nothing here needs to navigate to the Client entity itself,
 * only to check membership, so the simpler mapping avoids pulling in
 * Client's fields (and lazy-loading surprises) just to answer "does this
 * goal include this client".
 */
@Entity
@Table(name = "goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @Column(updatable = false, nullable = false)
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "adviser_id", nullable = false)
    private UUID adviserId;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false)
    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "goal_clients", joinColumns = @JoinColumn(name = "goal_id"))
    @Column(name = "client_id")
    @Builder.Default
    private Set<UUID> clientIds = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
