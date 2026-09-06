package com.afrofuturists.rsfinancial.domain;

/**
 * PENDING -> SENT happens automatically via the daily scheduler sweep.
 * DISMISSED is the only status a person sets directly (an adviser
 * marking a reminder handled/no-longer-relevant before it even fires).
 * A recurring reminder goes back to PENDING with an advanced due_date
 * once it fires - see RecurrenceInterval and ReminderService.
 */
public enum ReminderStatus {
    PENDING,
    SENT,
    DISMISSED
}
