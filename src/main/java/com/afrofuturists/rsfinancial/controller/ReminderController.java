package com.afrofuturists.rsfinancial.controller;

import com.afrofuturists.rsfinancial.dto.ReminderDtos.CreateReminderRequest;
import com.afrofuturists.rsfinancial.dto.ReminderDtos.ReminderResponse;
import com.afrofuturists.rsfinancial.security.StaffUserDetails;
import com.afrofuturists.rsfinancial.service.ReminderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(
            @Valid @RequestBody CreateReminderRequest request,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(reminderService.createReminder(request, principal.getId()));
    }

    @GetMapping
    public ResponseEntity<List<ReminderResponse>> getMyReminders(
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(reminderService.getRemindersForAdviser(principal.getId()));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ReminderResponse>> getRemindersForClient(
            @PathVariable UUID clientId,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(reminderService.getRemindersForClient(clientId, principal.getId()));
    }

    @PatchMapping("/{reminderId}/dismiss")
    public ResponseEntity<ReminderResponse> dismiss(
            @PathVariable UUID reminderId,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(reminderService.dismiss(reminderId, principal.getId()));
    }

    // Dev/ops convenience so the sweep can be exercised without waiting
    // for 06:00, or re-run manually if a deploy meant a day's sweep was
    // missed. Restricted to ADMIN - see the note on ReminderService
    // .processDueReminders on why this isn't callable by any adviser.
    @PostMapping("/process-due")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> processDueRemindersNow() {
        int processed = reminderService.processDueReminders(LocalDate.now());
        return ResponseEntity.ok(processed + " reminder(s) processed");
    }
}
