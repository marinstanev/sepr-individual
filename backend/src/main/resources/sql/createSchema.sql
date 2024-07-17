CREATE TABLE IF NOT EXISTS tournament
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date_of_start DATE NOT NULL,
    date_of_finish DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS breed
(
  id BIGINT PRIMARY KEY,
  name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS horse
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  -- Instead of an ENUM (H2 specific) this could also be done with a character string type and a check constraint.
  sex ENUM ('MALE', 'FEMALE') NOT NULL,
  date_of_birth DATE NOT NULL,
  height NUMERIC(4,2),
  weight NUMERIC(7,2),
  breed_id BIGINT REFERENCES breed(id)
);

CREATE TABLE IF NOT EXISTS participant
(
    id_tournament BIGINT REFERENCES tournament(id),
    id_participant BIGINT REFERENCES horse(id),
    entry_number BIGINT,
    round_reached BIGINT
);