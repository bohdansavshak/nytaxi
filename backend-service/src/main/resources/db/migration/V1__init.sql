-- V1__Create_tables.sql


-- Create the rate_code table if it doesn't exist
CREATE TABLE IF NOT EXISTS rate_code
(
    rate_code_id   INTEGER PRIMARY KEY,
    rate_code_name TEXT
);

-- Create the payment_type table if it doesn't exist -- this could be enum but I used the table to get the experience of limitation of r2dbc. It's not full featured orm.
CREATE TABLE IF NOT EXISTS payment_type
(
    payment_type_id INTEGER PRIMARY KEY,
    name            TEXT
);

-- Create the vendor table if it doesn't exist
CREATE TABLE IF NOT EXISTS vendor
(
    vendor_id INTEGER PRIMARY KEY,
    name      TEXT
);

-- Create the taxi_trip table if it doesn't exist
CREATE TABLE IF NOT EXISTS taxi_trip
(
    taxi_trip_id          SERIAL NOT NULL PRIMARY KEY,
    vendor_id             INTEGER,
    rate_code_id          INTEGER,
    payment_type_id       INTEGER,
    tpep_pickup_datetime  TEXT,
    tpep_dropoff_datetime TEXT,
    dropoff_day           INTEGER,
    dropoff_month         INTEGER,
    passenger_count       INTEGER,
    trip_distance         DECIMAL,
    pulocation_id         INTEGER,
    dolocation_id         INTEGER,
    store_and_fwd_flag    BOOLEAN,
    fare_amount           DECIMAL,
    extra                 DECIMAL,
    mta_tax               DECIMAL,
    improvement_surcharge DECIMAL,
    tip_amount            DECIMAL,
    tolls_amount          DECIMAL,
    total_amount          DECIMAL,
    CONSTRAINT fk_rate_code FOREIGN KEY (rate_code_id) REFERENCES rate_code (rate_code_id),
    CONSTRAINT fk_payment_type FOREIGN KEY (payment_type_id) REFERENCES payment_type (payment_type_id)
);