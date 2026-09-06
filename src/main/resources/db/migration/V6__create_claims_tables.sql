-- Claims: the short-term insurance motor-claim workflow from the problem
-- statement. Three tables:
--   claims               - the claim itself and its current state
--   claim_status_history - an append-only trail of every status change,
--                          since "keep everyone informed" over days/
--                          weeks means someone needs to see what
--                          happened and when, not just where it is now
--   claim_documents       - metadata for uploaded photos/licenses/sketch;
--                          the actual bytes live on disk (or later, cloud
--                          storage) - see FileStorageService

CREATE TABLE claims (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id               UUID NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    adviser_id              UUID NOT NULL REFERENCES staff_users(id),
    insurer_name            VARCHAR(120) NOT NULL,
    incident_at             TIMESTAMPTZ NOT NULL,
    description              TEXT,
    police_notified         BOOLEAN NOT NULL DEFAULT false,
    police_case_number      VARCHAR(100),
    witness_name            VARCHAR(255),
    witness_contact         VARCHAR(100),
    witness_statement_taken BOOLEAN NOT NULL DEFAULT false,
    driver_name             VARCHAR(255),
    vehicle_use             VARCHAR(20), -- PERSONAL | BUSINESS
    other_party_details     TEXT,
    status                  VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    claim_number            VARCHAR(100), -- set once the insurer issues one (step 1 of the lifecycle)
    claim_handler_name      VARCHAR(255),
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE claim_status_history (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_id    UUID NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    status      VARCHAR(30) NOT NULL,
    note        TEXT,
    changed_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE claim_documents (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_id          UUID NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    document_type     VARCHAR(30) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_path       VARCHAR(500) NOT NULL,
    uploaded_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_claims_client_id ON claims(client_id);
CREATE INDEX idx_claims_adviser_id ON claims(adviser_id);
CREATE INDEX idx_claim_status_history_claim_id ON claim_status_history(claim_id);
CREATE INDEX idx_claim_documents_claim_id ON claim_documents(claim_id);
