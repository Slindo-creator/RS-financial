package com.afrofuturists.rsfinancial.controller;

import com.afrofuturists.rsfinancial.dto.GoalDtos.CreateGoalRequest;
import com.afrofuturists.rsfinancial.dto.GoalDtos.GoalResponse;
import com.afrofuturists.rsfinancial.dto.GoalDtos.UpdateProgressRequest;
import com.afrofuturists.rsfinancial.security.StaffUserDetails;
import com.afrofuturists.rsfinancial.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @Valid @RequestBody CreateGoalRequest request,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(goalService.createGoal(request, principal.getId()));
    }

    // For the dashboard's per-client goal-progress view - a client's page
    // just calls this with its own id, whether the goal is individual or
    // shared shows up transparently via how many clientIds come back.
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<GoalResponse>> getGoalsForClient(
            @PathVariable UUID clientId,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(goalService.getGoalsForClient(clientId, principal.getId()));
    }

    @PatchMapping("/{goalId}/progress")
    public ResponseEntity<GoalResponse> updateProgress(
            @PathVariable UUID goalId,
            @Valid @RequestBody UpdateProgressRequest request,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        GoalResponse response = goalService.updateProgress(goalId, principal.getId(), request.currentAmount());
        return ResponseEntity.ok(response);
    }
}
