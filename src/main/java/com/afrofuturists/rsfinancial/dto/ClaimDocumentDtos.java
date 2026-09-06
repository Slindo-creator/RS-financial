package com.afrofuturists.rsfinancial.dto;

import com.afrofuturists.rsfinancial.domain.ClaimDocumentType;

import java.time.Instant;
import java.util.UUID;

public class ClaimDocumentDtos {

    public record ClaimDocumentResponse(
            UUID id,
            ClaimDocumentType documentType,
            String originalFilename,
            // A servable URL, not the on-disk path - see
            // FileStorageService and the static resource mapping in
            // WebMvcConfig that makes this URL actually resolve.
            String url,
            Instant uploadedAt
    ) {}
}
