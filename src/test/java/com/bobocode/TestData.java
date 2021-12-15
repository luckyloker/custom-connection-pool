package com.bobocode;

public class TestData {

    public static final String CREATE_TABLE = "CREATE TABLE comediants (id BIGSERIAL PRIMARY KEY, firstname VARCHAR(55) NOT NULL, lastname VARCHAR(55) NOT NULL, country VARCHAR(55));" +
            "CREATE SEQUENCE id_gen START 101;";
    public static final String INSERT_TEST_DATA = "INSERT INTO comediants (id, firstname, lastname, country) VALUES (nextval('id_gen'), 'Lui', 'C Key', 'USA');" +
            "INSERT INTO comediants (id, firstname, lastname, country) VALUES (nextval('id_gen'), 'Serhii', 'Lipko', 'Ukraine');";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS comediants; DROP SEQUENCE IF EXISTS id_gen;";
}
