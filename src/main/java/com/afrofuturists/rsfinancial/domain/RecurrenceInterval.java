package com.afrofuturists.rsfinancial.domain;

import java.time.LocalDate;

/**
 * NONE covers one-off tasks (e.g. "call this client back Tuesday").
 * Everything else is a recurring rule - nextDueDateFrom() is the single
 * place that knows how to advance each one, so the scheduler doesn't
 * need its own switch statement duplicating this logic.
 */
public enum RecurrenceInterval {
    NONE,
    MONTHLY,
    ANNUAL,
    EVERY_2_YEARS;

    public LocalDate nextDueDateFrom(LocalDate previousDueDate) {
        return switch (this) {
            case NONE -> null; // one-off reminders don't get rescheduled
            case MONTHLY -> previousDueDate.plusMonths(1);
            case ANNUAL -> previousDueDate.plusYears(1);
            case EVERY_2_YEARS -> previousDueDate.plusYears(2);
        };
    }
}
