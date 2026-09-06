package com.afrofuturists.rsfinancial.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Maps to claim_documents (V6 migration). storedPath is where
 * FileStorageService actually wrote the bytes (local disk today, cloud
 * storage later) - it's an implementation detail the API response
 * deliberately hides behind a served URL. See ClaimDtos.ClaimDocumentResponse.
 */
@Entity
@Table(name = "claim_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDocument {

    @Id
    @Column(updatable = false, nullable = false)
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "claim_id", nullable = false)
    private UUID claimId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private ClaimDocumentType documentType;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "stored_path", nullable = false)
    private String storedPath;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private Instant uploadedAt;
}
