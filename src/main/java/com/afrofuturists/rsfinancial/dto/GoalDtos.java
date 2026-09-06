package com.afrofuturists.rsfinancial.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public class GoalDtos {

    public record CreateGoalRequest(
            @NotBlank String title,
            String description,
            @NotNull @Positive BigDecimal targetAmount,
            LocalDate targetDate,
            // One clientId = individual goal, more than one = shared.
            // No upper bound - see the migration's note on why this
            // isn't a fixed two-client "shared goal" pairing.
            @NotEmpty Set<UUID> clientIds
    ) {}

    public record UpdateProgressRequest(
            @NotNull @PositiveOrZero BigDecimal currentAmount
    ) {}

    public record GoalResponse(
            UUID id,
            String title,
            String description,
            BigDecimal targetAmount,
            BigDecimal currentAmount,
            // 0-100, capped there even if currentAmount overshoots
            // targetAmount - a client can overfund a goal, but "142%"
            // in a progress bar is a UI bug, not a fact worth surfacing.
            int progressPercent,
            LocalDate targetDate,
            Set<UUID> clientIds
    ) {}
}
