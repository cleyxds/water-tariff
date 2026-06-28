CREATE TABLE tariff_tables (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    effective_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE consumer_categories (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(120) NOT NULL,
    tariff_table_id UUID NOT NULL,
    CONSTRAINT fk_consumer_category_tariff_table
        FOREIGN KEY (tariff_table_id)
        REFERENCES tariff_tables(id)
        ON DELETE CASCADE,
    CONSTRAINT uk_consumer_category_per_tariff_table
        UNIQUE (tariff_table_id, code)
);

CREATE TABLE consumption_ranges (
    id UUID PRIMARY KEY,
    start_m3 INTEGER NOT NULL,
    end_m3 INTEGER NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    consumer_category_id UUID NOT NULL,
    CONSTRAINT fk_consumption_range_consumer_category
        FOREIGN KEY (consumer_category_id)
        REFERENCES consumer_categories(id)
        ON DELETE CASCADE,
    CONSTRAINT ck_consumption_range_order
        CHECK (start_m3 <= end_m3),
    CONSTRAINT ck_consumption_range_price
        CHECK (unit_price >= 0)
);
