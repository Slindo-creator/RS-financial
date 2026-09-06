package com.afrofuturists.rsfinancial.service;

import com.afrofuturists.rsfinancial.domain.Client;
import com.afrofuturists.rsfinancial.domain.Goal;
import com.afrofuturists.rsfinancial.dto.GoalDtos.CreateGoalRequest;
import com.afrofuturists.rsfinancial.dto.GoalDtos.GoalResponse;
import com.afrofuturists.rsfinancial.repository.ClientRepository;
import com.afrofuturists.rsfinancial.repository.GoalRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final ClientRepository clientRepository;

    public GoalService(GoalRepository goalRepository, ClientRepository clientRepository) {
        this.goalRepository = goalRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public GoalResponse createGoal(CreateGoalRequest request, UUID adviserId) {
        // Every client named on the goal must actually be one of this
        // adviser's clients - otherwise an adviser could attach a goal
        // (and later, via updateProgress, write access) to someone
        // else's client just by guessing a UUID.
        for (UUID clientId : request.clientIds()) {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new NoSuchElementException("Client not found: " + clientId));
            if (!client.getAdviserId().equals(adviserId)) {
                throw new AccessDeniedException("Client " + clientId + " is not assigned to you");
            }
        }

        Goal goal = Goal.builder()
                .adviserId(adviserId)
                .title(request.title())
                .description(request.description())
                .targetAmount(request.targetAmount())
                .targetDate(request.targetDate())
                .clientIds(request.clientIds())
                .build();

        return toResponse(goalRepository.save(goal));
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getGoalsForClient(UUID clientId, UUID adviserId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client not found"));
        if (!client.getAdviserId().equals(adviserId)) {
            throw new AccessDeniedException("This client is not assigned to you");
        }

        return goalRepository.findByClientId(clientId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public GoalResponse updateProgress(UUID goalId, UUID adviserId, BigDecimal currentAmount) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NoSuchElementException("Goal not found"));

        if (!goal.getAdviserId().equals(adviserId)) {
            throw new AccessDeniedException("This goal is not yours to update");
        }

        goal.setCurrentAmount(currentAmount);
        return toResponse(goalRepository.save(goal));
    }

    private GoalResponse toResponse(Goal goal) {
        int progressPercent = goal.getTargetAmount().signum() == 0
                ? 0
                : goal.getCurrentAmount()
                        .divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .min(BigDecimal.valueOf(100))
                        .max(BigDecimal.ZERO)
                        .intValue();

        return new GoalResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                progressPercent,
                goal.getTargetDate(),
                goal.getClientIds()
        );
    }
}
