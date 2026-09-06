package com.afrofuturists.rsfinancial.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClientDtos {

    // Request payload for creating a client. adviserId is not accepted
    // here - it comes from the authenticated caller in the service, not
    // from client input, so an adviser can't create a client "for"
    // someone else by editing the request body.
    public record CreateClientRequest(
            @NotBlank String fullName,
            @NotBlank @Email String email,
            String phone,
            BigDecimal netWorthEstimate
    ) {}

    public record ClientResponse(
            UUID id,
            String fullName,
            String email,
            String phone,
            UUID adviserId,
            BigDecimal netWorthEstimate
    ) {}

    public record UpdateNetWorthRequest(
            @NotNull BigDecimal netWorthEstimate
    ) {}
}
