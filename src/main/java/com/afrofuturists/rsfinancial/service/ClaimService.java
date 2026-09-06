package com.afrofuturists.rsfinancial.service;

import com.afrofuturists.rsfinancial.domain.*;
import com.afrofuturists.rsfinancial.dto.ClaimDocumentDtos.ClaimDocumentResponse;
import com.afrofuturists.rsfinancial.dto.ClaimDtos.AccidentChecklistResponse;
import com.afrofuturists.rsfinancial.dto.ClaimDtos.ClaimResponse;
import com.afrofuturists.rsfinancial.dto.ClaimDtos.ClaimStatusHistoryEntry;
import com.afrofuturists.rsfinancial.dto.ClaimDtos.RegisterClaimRequest;
import com.afrofuturists.rsfinancial.repository.ClaimDocumentRepository;
import com.afrofuturists.rsfinancial.repository.ClaimRepository;
import com.afrofuturists.rsfinancial.repository.ClaimStatusHistoryRepository;
import com.afrofuturists.rsfinancial.repository.ClientRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimStatusHistoryRepository historyRepository;
    private final ClaimDocumentRepository documentRepository;
    private final ClientRepository clientRepository;
    private final FileStorageService fileStorageService;

    public ClaimService(ClaimRepository claimRepository,
                         ClaimStatusHistoryRepository historyRepository,
                         ClaimDocumentRepository documentRepository,
                         ClientRepository clientRepository,
                         FileStorageService fileStorageService) {
        this.claimRepository = claimRepository;
        this.historyRepository = historyRepository;
        this.documentRepository = documentRepository;
        this.clientRepository = clientRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Static reference content - what "tap Report an Accident or Loss"
     * returns immediately, straight from the problem statement's
     * gather-at-the-scene list. Nothing here is persisted or tied to a
     * claim; if this ever needs to be editable without a redeploy, move
     * it to a table, but a hardcoded list is the right amount of
     * engineering for content that doesn't change per client or adviser.
     */
    public AccidentChecklistResponse getAccidentChecklist() {
        return new AccidentChecklistResponse(
                List.of(
                        "Photos of the road surface and direction of travel",
                        "The address or nearest cross streets",
                        "Photos of all vehicles and people involved",
                        "License plates and registration discs",
                        "ID documentation of everyone involved",
                        "Witness names, contact details, plus a voice note if possible",
                        "Insurance details of the other parties"
                ),
                "Report to the police within 48 hours"
        );
    }

    @Transactional
    public ClaimResponse registerClaim(RegisterClaimRequest request, UUID adviserId) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new NoSuchElementException("Client not found: " + request.clientId()));
        if (!client.getAdviserId().equals(adviserId)) {
            throw new AccessDeniedException("This client is not assigned to you");
        }

        Claim claim = Claim.builder()
                .clientId(request.clientId())
                .adviserId(adviserId)
                .insurerName(request.insurerName())
                .incidentAt(request.incidentAt())
                .description(request.description())
                .policeNotified(request.policeNotified())
                .policeCaseNumber(request.policeCaseNumber())
                .witnessName(request.witnessName())
                .witnessContact(request.witnessContact())
                .witnessStatementTaken(request.witnessStatementTaken())
                .driverName(request.driverName())
                .vehicleUse(request.vehicleUse())
                .otherPartyDetails(request.otherPartyDetails())
                .build();
        claim = claimRepository.save(claim);

        recordHistory(claim.getId(), ClaimStatus.SUBMITTED, "Claim registered");

        return toResponse(claim);
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsForClient(UUID clientId, UUID adviserId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client not found"));
        if (!client.getAdviserId().equals(adviserId)) {
            throw new AccessDeniedException("This client is not assigned to you");
        }

        return claimRepository.findByClientId(clientId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClaimResponse getClaim(UUID claimId, UUID adviserId) {
        Claim claim = findOwnedClaim(claimId, adviserId);
        return toResponse(claim);
    }

    @Transactional
    public ClaimResponse updateStatus(UUID claimId, UUID adviserId, ClaimStatus newStatus,
                                       String note, String claimNumber, String claimHandlerName) {
        Claim claim = findOwnedClaim(claimId, adviserId);

        claim.setStatus(newStatus);
        if (claimNumber != null) {
            claim.setClaimNumber(claimNumber);
        }
        if (claimHandlerName != null) {
            claim.setClaimHandlerName(claimHandlerName);
        }
        claim = claimRepository.save(claim);

        recordHistory(claim.getId(), newStatus, note);

        return toResponse(claim);
    }

    @Transactional
    public ClaimDocumentResponse addDocument(UUID claimId, UUID adviserId,
                                              ClaimDocumentType documentType, MultipartFile file) {
        Claim claim = findOwnedClaim(claimId, adviserId);

        String storedPath = fileStorageService.store(file, "claims/" + claim.getId());
        ClaimDocument document = ClaimDocument.builder()
                .claimId(claim.getId())
                .documentType(documentType)
                .originalFilename(file.getOriginalFilename())
                .storedPath(storedPath)
                .build();
        document = documentRepository.save(document);

        return toDocumentResponse(document);
    }

    @Transactional(readOnly = true)
    public List<ClaimDocumentResponse> getDocuments(UUID claimId, UUID adviserId) {
        findOwnedClaim(claimId, adviserId); // ownership check, result unused deliberately
        return documentRepository.findByClaimId(claimId).stream()
                .map(this::toDocumentResponse)
                .toList();
    }

    private Claim findOwnedClaim(UUID claimId, UUID adviserId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new NoSuchElementException("Claim not found"));
        if (!claim.getAdviserId().equals(adviserId)) {
            throw new AccessDeniedException("This claim is not yours");
        }
        return claim;
    }

    private void recordHistory(UUID claimId, ClaimStatus status, String note) {
        historyRepository.save(ClaimStatusHistory.builder()
                .claimId(claimId)
                .status(status)
                .note(note)
                .build());
    }

    private ClaimResponse toResponse(Claim claim) {
        List<ClaimStatusHistoryEntry> history = historyRepository
                .findByClaimIdOrderByChangedAtAsc(claim.getId()).stream()
                .map(h -> new ClaimStatusHistoryEntry(h.getStatus(), h.getNote(), h.getChangedAt()))
                .toList();

        return new ClaimResponse(
                claim.getId(),
                claim.getClientId(),
                claim.getInsurerName(),
                claim.getIncidentAt(),
                claim.getDescription(),
                claim.isPoliceNotified(),
                claim.getPoliceCaseNumber(),
                claim.getWitnessName(),
                claim.getWitnessContact(),
                claim.isWitnessStatementTaken(),
                claim.getDriverName(),
                claim.getVehicleUse(),
                claim.getOtherPartyDetails(),
                claim.getStatus(),
                claim.getClaimNumber(),
                claim.getClaimHandlerName(),
                history
        );
    }

    private ClaimDocumentResponse toDocumentResponse(ClaimDocument document) {
        return new ClaimDocumentResponse(
                document.getId(),
                document.getDocumentType(),
                document.getOriginalFilename(),
                fileStorageService.toServedUrl(document.getStoredPath()),
                document.getUploadedAt()
        );
    }
}
