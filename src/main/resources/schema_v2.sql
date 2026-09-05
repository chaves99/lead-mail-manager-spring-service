CREATE TABLE IF NOT EXISTS lead(
    email VARCHAR(100) PRIMARY KEY,
);

CREATE INDEX company_email_index ON company (id);


CREATE TABLE IF NOT EXISTS company(
    cnpj BIGINT PRIMARY KEY,
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
    specialSituation VARCHAR,
    specialSituationDate VARCHAR,
    email VARCHAR(100) NOT NULL REFERENCES lead(email)
);
CREATE INDEX company_cnpj_index ON company (cnpj);

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
    email BIGINT NOT NULL REFERENCES company(id),
    success BOOLEAN DEFAULT FALSE,
    error_msg TEXT,
    campaign_id BIGINT NOT NULL REFERENCES campaign(id)
);

CREATE TABLE IF NOT EXISTS cnae(
    code SERIAL PRIMARY KEY,
    description TEXT
);

CREATE TABLE IF NOT EXISTS potential_customer(
    email VARCHAR(100) PRIMARY KEY,
    
);

