-- Update RUN table definition
ALTER TABLE run
    ADD COLUMN IF NOT EXISTS finished boolean DEFAULT true,
    ALTER COLUMN comment TYPE text;
ALTER TABLE run
    RENAME COLUMN end_date TO last_end_date;
ALTER TABLE run
    RENAME COLUMN start_date TO last_start_date;
