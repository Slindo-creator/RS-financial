package com.afrofuturists.rsfinancial.dto;

import com.afrofuturists.rsfinancial.domain.RecipientType;
import com.afrofuturists.rsfinancial.domain.RecurrenceInterval;
import com.afrofuturists.rsfinancial.domain.ReminderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class ReminderDtos {

    public record CreateReminderRequest(
            @NotBlank String title,
            String description,
            @NotNull LocalDate dueDate,
            @NotNull RecipientType recipient,
            // Defaults to NONE (one-off) if the caller omits it entirely -
            // most ad hoc reminders an adviser types in won't recur.
            RecurrenceInterval recurrenceInterval,
            // Null = not tied to any one client (e.g. "retirement fee
            // renewal -> us").
            UUID clientId
    ) {
        public RecurrenceInterval recurrenceIntervalOrDefault() {
            return recurrenceInterval == null ? RecurrenceInterval.NONE : recurrenceInterval;
        }
    }

    public record ReminderResponse(
            UUID id,
            String title,
            String description,
            LocalDate dueDate,
            RecipientType recipient,
            RecurrenceInterval recurrenceInterval,
            ReminderStatus status,
            UUID clientId,
            Instant lastTriggeredAt
    ) {}
}
