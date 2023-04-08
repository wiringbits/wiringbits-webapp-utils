
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

-- start at max value. first insert will be at max, second should fail.
-- ERROR: integer out of range
CREATE SEQUENCE serial_table_overflow_seq START 2147483647;
CREATE TABLE serial_table_overflow (
    id integer NOT NULL DEFAULT nextval('serial_table_overflow_seq') PRIMARY KEY,
    name TEXT NOT NULL
);
ALTER SEQUENCE serial_table_overflow_seq OWNED BY serial_table_overflow.id;

-- start at max value. first insert will be at max, second should fail.
-- ERROR: nextval: reached maximum value of sequence "big_serial_table_overflow_seq" (9223372036854775807)
CREATE SEQUENCE big_serial_table_overflow_seq START 9223372036854775807;
CREATE TABLE big_serial_table_overflow (
    id bigint NOT NULL DEFAULT nextval('big_serial_table_overflow_seq') PRIMARY KEY,
    name TEXT NOT NULL
);
ALTER SEQUENCE big_serial_table_overflow_seq OWNED BY big_serial_table_overflow.id;
