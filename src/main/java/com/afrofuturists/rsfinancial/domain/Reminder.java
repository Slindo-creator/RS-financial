package com.afrofuturists.rsfinancial.domain;

import java.time.Instant;
import java.time.LocalDate;
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
 * Maps to reminders (V4 migration). clientId is nullable on purpose -
 * some reminders (e.g. "retirement fee renewal -> us") aren't about any
 * one client at all.
 */
@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder {

    @Id
    @Column(updatable = false, nullable = false)
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "adviser_id", nullable = false)
    private UUID adviserId;

    @Column(name = "client_id")
    private UUID clientId;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipientType recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_interval", nullable = false)
    @Builder.Default
    private RecurrenceInterval recurrenceInterval = RecurrenceInterval.NONE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReminderStatus status = ReminderStatus.PENDING;

    @Column(name = "last_triggered_at")
    private Instant lastTriggeredAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
