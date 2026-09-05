CREATE TABLE IF NOT EXISTS company(
    id BIGSERIAL PRIMARY KEY,
    cnpj BIGINT UNIQUE,
    mother_branch_identifier VARCHAR,
    fantasy_name VARCHAR,
    registry_situation VARCHAR,
    registry_situation_date VARCHAR,
    reasonRegistrySituation VARCHAR,
    outdoorCityName VARCHAR,
    country VARCHAR,
    startActivityDate VARCHAR,
    cnaeTaxPrimary VARCHAR,
    cnaeTaxSecondary VARCHAR,
    addressStreetType VARCHAR,
    addressStreet VARCHAR,
    addressNumber VARCHAR,
    addressComplement VARCHAR,
    addressNeighborhood VARCHAR,
    addressCode VARCHAR,
    addressState VARCHAR,
    addressCity VARCHAR,
    telephoneCode1 VARCHAR,
    telephone1 VARCHAR,
    telephoneCode2 VARCHAR,
    telephone2 VARCHAR,
    email VARCHAR,
    specialSituation VARCHAR,
    specialSituationDate VARCHAR
);

CREATE TABLE IF NOT EXISTS template(
    id BIGSERIAL PRIMARY KEY,
    subject TEXT,
    body TEXT,
    name VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS campaign(
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL REFERENCES template(id),
    description TEXT,
    company_count INT,
    created_at TIMESTAMP NOT NULL,
);

CREATE TABLE IF NOT EXISTS campaign_row(
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES company(id),
    success BOOLEAN DEFAULT FALSE,
    error_msg TEXT,
    campaign_parent_id BIGINT NOT NULL REFERENCES campaign(id)
);

CREATE TABLE IF NOT EXISTS cnae(
    code SERIAL PRIMARY KEY,
    description TEXT
);

CREATE TABLE IF NOT EXISTS potential_customer(
    email VARCHAR(100) PRIMARY KEY,
    
);

CREATE INDEX company_id_index ON company (id);
CREATE INDEX company_email_index ON company (id);
