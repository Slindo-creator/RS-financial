package com.afrofuturists.rsfinancial.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.afrofuturists.rsfinancial.dto.ClientDtos.ClientResponse;
import com.afrofuturists.rsfinancial.dto.ClientDtos.CreateClientRequest;
import com.afrofuturists.rsfinancial.dto.ClientDtos.UpdateNetWorthRequest;
import com.afrofuturists.rsfinancial.security.StaffUserDetails;
import com.afrofuturists.rsfinancial.service.ClientService;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // @AuthenticationPrincipal is populated by the JWT filter chain
    // already in place (AuthTokenFilter -> StaffUserDetailsService) -
    // the controller never parses the token or looks anything up itself.
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody CreateClientRequest request,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        ClientResponse response = clientService.createClient(request, principal.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getMyClients(
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(clientService.getClientsForAdviser(principal.getId()));
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponse> getClient(
            @PathVariable UUID clientId,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(clientService.getClientOwnedByAdviser(clientId, principal.getId()));
    }

    @PatchMapping("/{clientId}/net-worth")
    public ResponseEntity<ClientResponse> updateNetWorth(
            @PathVariable UUID clientId,
            @Valid @RequestBody UpdateNetWorthRequest request,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        ClientResponse response = clientService.updateNetWorth(
                clientId, principal.getId(), request.netWorthEstimate());
        return ResponseEntity.ok(response);
    }
}
