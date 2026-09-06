package com.afrofuturists.rsfinancial.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afrofuturists.rsfinancial.domain.Client;
import com.afrofuturists.rsfinancial.dto.ClientDtos.ClientResponse;
import com.afrofuturists.rsfinancial.dto.ClientDtos.CreateClientRequest;
import com.afrofuturists.rsfinancial.repository.ClientRepository;

/**
 * Owns the business rules for clients - the controller never touches
 * ClientRepository directly. Right now that's mostly "an adviser only
 * sees their own clients", but this is where FNA-driven validation,
 * goal-tracking rules, etc. belong as they get built, rather than
 * leaking into the controller or being duplicated across callers.
 */
@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public ClientResponse createClient(CreateClientRequest request, UUID adviserId) {
        if (clientRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("A client with this email already exists");
        }

        Client client = Client.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .adviserId(adviserId)
                .netWorthEstimate(request.netWorthEstimate())
                .build();

        Client saved = clientRepository.save(client);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getClientsForAdviser(UUID adviserId) {
        return clientRepository.findByAdviserId(adviserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientOwnedByAdviser(UUID clientId, UUID adviserId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client not found"));

        // Ownership check lives here, not in the controller or a query
        // filter, so every call path (this one, future PATCH/DELETE
        // endpoints) enforces it the same way.
        if (!client.getAdviserId().equals(adviserId)) {
            throw new AccessDeniedException("This client is not assigned to you");
        }

        return toResponse(client);
    }

    @Transactional
    public ClientResponse updateNetWorth(UUID clientId, UUID adviserId, BigDecimal netWorthEstimate) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client not found"));

        if (!client.getAdviserId().equals(adviserId)) {
            throw new AccessDeniedException("This client is not assigned to you");
        }

        client.setNetWorthEstimate(netWorthEstimate);
        return toResponse(clientRepository.save(client));
    }

    private ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getFullName(),
                client.getEmail(),
                client.getPhone(),
                client.getAdviserId(),
                client.getNetWorthEstimate()
        );
    }
}
