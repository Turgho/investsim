CREATE TABLE simulations (
    id              BIGSERIAL       PRIMARY KEY,
    initial_value   DECIMAL(19,2)   NOT NULL,
    months          INTEGER         NOT NULL,
    investment_type VARCHAR(20)     NOT NULL,
    cdi_percentage  DECIMAL(5,2),
    gross_amount    DECIMAL(19,2)   NOT NULL,
    income_tax      DECIMAL(19,2)   NOT NULL,
    net_amount      DECIMAL(19,2)   NOT NULL,
    net_return      DECIMAL(19,2)   NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);
