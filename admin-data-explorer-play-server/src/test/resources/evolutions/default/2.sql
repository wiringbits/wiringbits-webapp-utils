
-- !Ups

CREATE TABLE uuid_table (
    id UUID NOT NULL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE serial_table (
    id SERIAL NOT NULL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE big_serial_table (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name TEXT NOT NULL
);
