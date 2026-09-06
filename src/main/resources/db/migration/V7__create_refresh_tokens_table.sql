-- Refresh tokens: lets a logged-in user get a new access token without
-- re-entering credentials, as long as they haven't explicitly logged out
-- and the refresh token itself hasn't expired. Only the SHA-256 hash of
-- the raw token is stored - same reasoning as staff_users.password_hash
-- (V1) - so a database dump alone can't be used to impersonate a user.

CREATE TABLE refresh_tokens (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_user_id  UUID NOT NULL REFERENCES staff_users(id) ON DELETE CASCADE,
    token_hash     VARCHAR(64) NOT NULL UNIQUE, -- hex-encoded SHA-256, always 64 chars
    expires_at     TIMESTAMPTZ NOT NULL,
    revoked        BOOLEAN NOT NULL DEFAULT false,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_staff_user_id ON refresh_tokens(staff_user_id);
