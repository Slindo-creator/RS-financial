package com.afrofuturists.rsfinancial.dto;

import jakarta.validation.constraints.NotBlank;

import com.afrofuturists.rsfinancial.domain.StaffRole;

public class AuthDtos {

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record AuthResponse(
            String token,
            String username,
            StaffRole role,
            long expiresInMs
    ) {}
}
