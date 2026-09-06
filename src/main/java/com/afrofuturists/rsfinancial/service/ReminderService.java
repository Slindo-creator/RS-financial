package com.afrofuturists.rsfinancial.service;

import com.afrofuturists.rsfinancial.domain.Client;
import com.afrofuturists.rsfinancial.domain.Reminder;
import com.afrofuturists.rsfinancial.domain.ReminderStatus;
import com.afrofuturists.rsfinancial.dto.ReminderDtos.CreateReminderRequest;
import com.afrofuturists.rsfinancial.dto.ReminderDtos.ReminderResponse;
import com.afrofuturists.rsfinancial.repository.ClientRepository;
import com.afrofuturists.rsfinancial.repository.ReminderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ReminderService {

    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);

    private final ReminderRepository reminderRepository;
    private final ClientRepository clientRepository;

    public ReminderService(ReminderRepository reminderRepository, ClientRepository clientRepository) {
        this.reminderRepository = reminderRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public ReminderResponse createReminder(CreateReminderRequest request, UUID adviserId) {
        if (request.clientId() != null) {
            Client client = clientRepository.findById(request.clientId())
                    .orElseThrow(() -> new NoSuchElementException("Client not found: " + request.clientId()));
            if (!client.getAdviserId().equals(adviserId)) {
                throw new AccessDeniedException("This client is not assigned to you");
            }
        }

        Reminder reminder = Reminder.builder()
                .adviserId(adviserId)
                .clientId(request.clientId())
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .recipient(request.recipient())
                .recurrenceInterval(request.recurrenceIntervalOrDefault())
                .build();

        return toResponse(reminderRepository.save(reminder));
    }

    @Transactional(readOnly = true)
    public List<ReminderResponse> getRemindersForAdviser(UUID adviserId) {
        return reminderRepository.findByAdviserId(adviserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReminderResponse> getRemindersForClient(UUID clientId, UUID adviserId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client not found"));
        if (!client.getAdviserId().equals(adviserId)) {
            throw new AccessDeniedException("This client is not assigned to you");
        }

        return reminderRepository.findByClientId(clientId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ReminderResponse dismiss(UUID reminderId, UUID adviserId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new NoSuchElementException("Reminder not found"));
        if (!reminder.getAdviserId().equals(adviserId)) {
            throw new AccessDeniedException("This reminder is not yours");
        }

        reminder.setStatus(ReminderStatus.DISMISSED);
        return toResponse(reminderRepository.save(reminder));
    }

    /**
     * Called once a day by ReminderScheduler - not exposed as an HTTP
     * endpoint, since "process everything due today" isn't something a
     * caller should be able to trigger on demand (it would let anyone
     * force-fire another adviser's reminders early, and running it twice
     * in the same day would double-advance recurring due dates).
     */
    @Transactional
    public int processDueReminders(LocalDate today) {
        List<Reminder> due = reminderRepository.findByStatusAndDueDateLessThanEqual(
                ReminderStatus.PENDING, today);

        for (Reminder reminder : due) {
            // Actual delivery (email/SMS/push) is a separate concern this
            // service deliberately doesn't own - see the note on
            // ReminderScheduler for where that plugs in later. For now,
            // "sent" means "the system has recorded that this fired".
            log.info("Reminder due: '{}' -> {} (client={})",
                    reminder.getTitle(), reminder.getRecipient(), reminder.getClientId());

            reminder.setLastTriggeredAt(Instant.now());

            LocalDate nextDueDate = reminder.getRecurrenceInterval().nextDueDateFrom(reminder.getDueDate());
            if (nextDueDate == null) {
                reminder.setStatus(ReminderStatus.SENT);
            } else {
                reminder.setDueDate(nextDueDate);
                reminder.setStatus(ReminderStatus.PENDING);
            }

            reminderRepository.save(reminder);
        }

        return due.size();
    }

    private ReminderResponse toResponse(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getTitle(),
                reminder.getDescription(),
                reminder.getDueDate(),
                reminder.getRecipient(),
                reminder.getRecurrenceInterval(),
                reminder.getStatus(),
                reminder.getClientId(),
                reminder.getLastTriggeredAt()
        );
    }
}
