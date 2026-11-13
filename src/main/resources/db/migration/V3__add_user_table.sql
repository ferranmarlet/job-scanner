CREATE TABLE users (
    id          UUID PRIMARY KEY,
    external_id TEXT NOT NULL UNIQUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
)
