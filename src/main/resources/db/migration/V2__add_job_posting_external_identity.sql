ALTER TABLE job_posting
    ADD COLUMN external_id        TEXT,
    ADD COLUMN external_source_id TEXT NOT NULL,
    ADD CONSTRAINT uq_job_posting_external_source_id_external_id UNIQUE (external_source_id, external_id);
