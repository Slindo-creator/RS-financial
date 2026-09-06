package com.afrofuturists.rsfinancial.repository;

import com.afrofuturists.rsfinancial.domain.Reminder;
import com.afrofuturists.rsfinancial.domain.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    List<Reminder> findByAdviserId(UUID adviserId);

    List<Reminder> findByClientId(UUID clientId);

    // What the daily scheduler sweep runs - every reminder that's still
    // PENDING and due today or earlier (covers anything missed if the
    // job didn't run on its exact due date, e.g. the app was down).
    List<Reminder> findByStatusAndDueDateLessThanEqual(ReminderStatus status, LocalDate date);
}
