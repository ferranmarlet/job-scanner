CREATE TABLE job_posting (
    id              UUID PRIMARY KEY,
    title           TEXT NOT NULL,
    company         TEXT,
    location        TEXT,
    work_mode       VARCHAR(16),
    seniority       VARCHAR(16),
    salary_min      INTEGER,
    salary_max      INTEGER,
    salary_currency CHAR(3),
    source_url      TEXT NOT NULL,
    status          VARCHAR(16) NOT NULL,
    discovered_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_job_posting_source_url UNIQUE (source_url)
);

CREATE TABLE job_posting_skill (
    job_posting_id UUID NOT NULL REFERENCES job_posting (id) ON DELETE CASCADE,
    skill          TEXT NOT NULL,
    PRIMARY KEY (job_posting_id, skill)
);
