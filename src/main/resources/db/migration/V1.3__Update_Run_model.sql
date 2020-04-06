-- Update RUN table definition
ALTER TABLE run
    ADD COLUMN IF NOT EXISTS finished boolean,
    ALTER COLUMN comment TYPE text;
