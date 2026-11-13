CREATE TABLE resumes (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL UNIQUE,
    file_link   TEXT NOT NULL UNIQUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_resumes_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
)
