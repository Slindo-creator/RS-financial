-- Service requests: the catch-all for the problem statement's "other
-- tasks" list (change of address, change of bank details, policy
-- document / IRP5 / broker letter requests, consultation requests,
-- client info collection, balance sheet & income statement). One table
-- rather than eight - the workflow (submitted -> in progress ->
-- completed/rejected) is identical across all of them; only the payload
-- in `details` differs per type.

CREATE TABLE service_requests (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id           UUID NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    adviser_id          UUID NOT NULL REFERENCES staff_users(id),
    request_type        VARCHAR(40) NOT NULL,
    -- Freeform JSON text rather than per-type columns - e.g. change of
    -- address needs a new address, an IRP5 request needs a tax year,
    -- and forcing every type through the same fixed columns would mean
    -- most columns are null for most rows. The frontend knows what
    -- shape to expect per request_type and renders/parses accordingly.
    details             TEXT,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    resolution_notes    TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at        TIMESTAMPTZ
);

CREATE INDEX idx_service_requests_client_id ON service_requests(client_id);
CREATE INDEX idx_service_requests_adviser_id ON service_requests(adviser_id);
CREATE INDEX idx_service_requests_status ON service_requests(status);
