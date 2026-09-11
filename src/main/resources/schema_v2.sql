-- the lead way of structure things will be
-- centered by the email

CREATE TABLE IF NOT EXISTS lead(
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE,
    send_quantity INT NOT NULL DEFAULT 0,
    last_send DATE,
    open INT NOT NULL DEFAULT FALSE,
    click INT NOT NULL DEFAULT FALSE,
    last_click_date DATE
);
CREATE INDEX lead_id_index ON lead (id);
CREATE INDEX lead_email_index ON lead (email);

CREATE TABLE IF NOT EXISTS establishment(
    basic_cnpj BIGINT NOT NULL,
    order_cnpj INT NOT NULL,
    cnpj_verification_digit INT NOT NULL,
    mother_branch_identifier VARCHAR,
    fantasy_name VARCHAR,
    registry_situation VARCHAR,
    registry_situation_date VARCHAR,
    reason_registry_situation VARCHAR,
    outdoor_city_name VARCHAR,
    country VARCHAR,
    start_activity_date VARCHAR,
    cnae_tax_primary VARCHAR,
    cnae_tax_secondary VARCHAR,
    address_street_type VARCHAR,
    address_street VARCHAR,
    address_number VARCHAR,
    address_complement VARCHAR,
    address_neighborhood VARCHAR,
    address_code VARCHAR,
    address_state VARCHAR,
    address_city VARCHAR,
    telephone_code1 VARCHAR,
    telephone1 VARCHAR,
    telephone_code2 VARCHAR,
    telephone2 VARCHAR,
    special_situation VARCHAR,
    special_situation_date VARCHAR,
    PRIMARY KEY (basic_cnpj, order_cnpj, cnpj_verification_digit),
    lead_id BIGINT NOT NULL REFERENCES lead(id)
);
CREATE INDEX establishment_cnpj_index ON establishment (basic_cnpj);

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
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS campaign_row(
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT NOT NULL REFERENCES lead(id),
    success BOOLEAN DEFAULT FALSE,
    error_msg TEXT,
    send_date DATE NOT NULL DEFAULT CURRENT_DATE,
    opened BOOLEAN NOT NULL DEFAULT FALSE,
    clicked BOOLEAN NOT NULL DEFAULT FALSE,
    clicked_date DATE,
    campaign_id BIGINT NOT NULL REFERENCES campaign(id)
);
CREATE INDEX campaign_row_index ON campaign_row(id);

CREATE TABLE IF NOT EXISTS cnae(
    code SERIAL PRIMARY KEY,
    description TEXT
);

