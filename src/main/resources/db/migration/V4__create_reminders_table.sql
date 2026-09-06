-- Reminders: covers both one-off tasks an adviser creates ad hoc, and
-- recurring rules (e.g. "insurance valuation certificate, every 2
-- years"). Recurrence is modelled as "when this fires, compute the next
-- due_date and reset to PENDING" rather than a separate rules table -
-- simpler, and every recurring reminder is still just a row you can
-- see, dismiss, or reassign like any other.

CREATE TABLE reminders (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    adviser_id          UUID NOT NULL REFERENCES staff_users(id),
    client_id           UUID REFERENCES clients(id) ON DELETE CASCADE,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    due_date            DATE NOT NULL,
    recipient           VARCHAR(20) NOT NULL, -- ADVISER | CLIENT | BOTH
    recurrence_interval VARCHAR(20) NOT NULL DEFAULT 'NONE', -- NONE | MONTHLY | ANNUAL | EVERY_2_YEARS
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING | SENT | DISMISSED
    last_triggered_at   TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- The scheduler's daily sweep is exactly "give me every PENDING reminder
-- due today or earlier" - this index is what makes that cheap once the
-- table has years of reminders in it instead of a handful.
CREATE INDEX idx_reminders_status_due_date ON reminders(status, due_date);
CREATE INDEX idx_reminders_adviser_id ON reminders(adviser_id);
CREATE INDEX idx_reminders_client_id ON reminders(client_id);
