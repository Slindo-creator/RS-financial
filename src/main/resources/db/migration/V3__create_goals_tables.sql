-- Goals: a savings/investment target an adviser sets up for one client
-- (individual) or several (shared - e.g. a couple's joint retirement
-- goal). That's why there's no single owning client_id on `goals`
-- itself - ownership is expressed entirely through goal_clients.

CREATE TABLE goals (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    adviser_id      UUID NOT NULL REFERENCES staff_users(id),
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    target_amount   NUMERIC(14,2) NOT NULL,
    current_amount  NUMERIC(14,2) NOT NULL DEFAULT 0,
    target_date     DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_goals_target_amount_positive CHECK (target_amount > 0)
);

-- Join table rather than a nullable second client_id column on `goals` -
-- that would cap a "shared" goal at exactly two clients, which is an
-- arbitrary limit the problem statement doesn't ask for.
CREATE TABLE goal_clients (
    goal_id     UUID NOT NULL REFERENCES goals(id) ON DELETE CASCADE,
    client_id   UUID NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    PRIMARY KEY (goal_id, client_id)
);

CREATE INDEX idx_goals_adviser_id ON goals(adviser_id);
CREATE INDEX idx_goal_clients_client_id ON goal_clients(client_id);
