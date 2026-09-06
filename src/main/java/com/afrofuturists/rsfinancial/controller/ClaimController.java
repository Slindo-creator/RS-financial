package com.afrofuturists.rsfinancial.controller;

import com.afrofuturists.rsfinancial.domain.ClaimDocumentType;
import com.afrofuturists.rsfinancial.dto.ClaimDocumentDtos.ClaimDocumentResponse;
import com.afrofuturists.rsfinancial.dto.ClaimDtos.AccidentChecklistResponse;
import com.afrofuturists.rsfinancial.dto.ClaimDtos.ClaimResponse;
import com.afrofuturists.rsfinancial.dto.ClaimDtos.RegisterClaimRequest;
import com.afrofuturists.rsfinancial.dto.ClaimDtos.UpdateClaimStatusRequest;
import com.afrofuturists.rsfinancial.security.StaffUserDetails;
import com.afrofuturists.rsfinancial.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    // No clientId/claimId here on purpose - this is the "tap Report an
    // Accident" moment, before any claim exists yet. Still requires
    // auth like everything else in this API (see WebMvcConfig's note on
    // why /uploads isn't public either) - there's no client-facing login
    // yet, only staff, so for now an adviser fetches this on the
    // client's behalf.
    @GetMapping("/accident-checklist")
    public ResponseEntity<AccidentChecklistResponse> getAccidentChecklist() {
        return ResponseEntity.ok(claimService.getAccidentChecklist());
    }

    @PostMapping
    public ResponseEntity<ClaimResponse> registerClaim(
            @Valid @RequestBody RegisterClaimRequest request,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(claimService.registerClaim(request, principal.getId()));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ClaimResponse>> getClaimsForClient(
            @PathVariable UUID clientId,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(claimService.getClaimsForClient(clientId, principal.getId()));
    }

    @GetMapping("/{claimId}")
    public ResponseEntity<ClaimResponse> getClaim(
            @PathVariable UUID claimId,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(claimService.getClaim(claimId, principal.getId()));
    }

    @PatchMapping("/{claimId}/status")
    public ResponseEntity<ClaimResponse> updateStatus(
            @PathVariable UUID claimId,
            @Valid @RequestBody UpdateClaimStatusRequest request,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        ClaimResponse response = claimService.updateStatus(
                claimId, principal.getId(), request.status(), request.note(),
                request.claimNumber(), request.claimHandlerName());
        return ResponseEntity.ok(response);
    }

    // multipart/form-data, not JSON - see the Postman notes for how this
    // differs from every other POST in this API.
    @PostMapping(value = "/{claimId}/documents", consumes = "multipart/form-data")
    public ResponseEntity<ClaimDocumentResponse> uploadDocument(
            @PathVariable UUID claimId,
            @RequestParam ClaimDocumentType documentType,
            @RequestPart MultipartFile file,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(claimService.addDocument(claimId, principal.getId(), documentType, file));
    }

    @GetMapping("/{claimId}/documents")
    public ResponseEntity<List<ClaimDocumentResponse>> getDocuments(
            @PathVariable UUID claimId,
            @AuthenticationPrincipal StaffUserDetails principal
    ) {
        return ResponseEntity.ok(claimService.getDocuments(claimId, principal.getId()));
    }
}
