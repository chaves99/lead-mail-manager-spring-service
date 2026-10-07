-- the lead way of structure things will be
-- centered by the email

CREATE TABLE IF NOT EXISTS lead(
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE,
    send_quantity INT NOT NULL DEFAULT 0,
    last_send DATE,
    open INT NOT NULL DEFAULT FALSE,
    click INT NOT NULL DEFAULT FALSE,
    last_click_date DATE,
    unsubscribed BOOLEAN DEFAULT FALSE,
    unreachable BOOLEAN
);
CREATE INDEX lead_id_index ON lead (id);
CREATE INDEX lead_email_index ON lead (email);

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
    send_date TIMESTAMP NOT NULL DEFAULT CURRENT_DATE,
    opened BOOLEAN NOT NULL DEFAULT FALSE,
    clicked BOOLEAN NOT NULL DEFAULT FALSE,
    clicked_date DATE,
    status SMALLINT,
    campaign_id BIGINT NOT NULL REFERENCES campaign(id)
);
CREATE INDEX campaign_row_index ON campaign_row(id);

