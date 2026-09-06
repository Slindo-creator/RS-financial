-- Clients: Royal Square's customers being advised. Deliberately separate
-- from staff_users (see V1's comment) - a client is not something that
-- logs in with this table's credentials, at least not yet.

CREATE TABLE clients (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name           VARCHAR(255) NOT NULL,
    email               VARCHAR(255) NOT NULL,
    phone               VARCHAR(50),
    adviser_id          UUID NOT NULL REFERENCES staff_users(id),
    net_worth_estimate  NUMERIC(14,2),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_clients_email UNIQUE (email)
);

CREATE INDEX idx_clients_adviser_id ON clients(adviser_id);
