-- Staff users: the people who log into this system (advisers, compliance
-- officers, admins) - NOT the same concept as `clients` (Royal Square's
-- customers being advised), which will be its own table in a later
-- migration once the client domain is ported over to JPA. Keeping these
-- separate from day one avoids the collision the earlier schema.sql
-- draft had, where "clients" was reused to mean "people who log in".

CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- for gen_random_uuid()

CREATE TABLE staff_users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username        VARCHAR(100) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(255) NOT NULL,
    role            VARCHAR(30) NOT NULL, -- ADVISER | COMPLIANCE_OFFICER | ADMIN
    enabled         BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_staff_users_username UNIQUE (username)
);
