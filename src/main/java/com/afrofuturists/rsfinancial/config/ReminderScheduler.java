package com.afrofuturists.rsfinancial.config;

import com.afrofuturists.rsfinancial.service.ReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * The only thing this class does is decide WHEN - all the actual logic
 * (what counts as due, how recurrence advances) lives in ReminderService
 * so it stays unit-testable without waiting on a cron trigger.
 *
 * Runs once a day at 06:00 server time. Delivery (actually emailing/
 * SMSing the adviser or client) isn't implemented yet - that's a
 * separate NotificationService this would call per-reminder once one
 * exists, rather than something to bolt onto this class directly.
 */
@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final ReminderService reminderService;

    public ReminderScheduler(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void sweepDueReminders() {
        int processed = reminderService.processDueReminders(LocalDate.now());
        log.info("Reminder sweep complete: {} reminder(s) processed", processed);
    }
}
