DROP TABLE IF EXISTS artists;
CREATE TABLE artists
(
    id          IDENTITY    NOT NULL PRIMARY KEY,
    first_name  VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    last_name   VARCHAR(50) NOT NULL,
    category    VARCHAR(20) NOT NULL CHECK (CATEGORY IN ('ACTOR', 'PAINTER', 'SCULPTOR')),
    birthday    DATE,
    email       VARCHAR(50),
    notes       VARCHAR(200),
    updated_at  TIMESTAMP,
    created_at  TIMESTAMP
);