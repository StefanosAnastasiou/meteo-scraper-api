package com.emperium.config;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.apache.log4j.Logger;


/**
 * @author Steafanos Anastasiou
 */
public class LiquibaseRunner {
    private static Logger logger = Logger.getLogger(LiquibaseRunner.class);

    public static void run() throws LiquibaseException {
        String url = System.getenv("DATABASE_URL");
        String user = System.getenv("DATABASE_USERNAME");
        String password = System.getenv("DATABASE_PASSWORD");
//        String url = "jdbc:postgresql://database-meteo:5332/weatherDB?createDatabaseIfNotExist=true";
//        String url = "jdbc:postgresql://localhost:5332/weatherDB?createDatabaseIfNotExist=true";
//        String user = "root";
//        String password = "password";

        Database database = DatabaseFactory.getInstance().openDatabase(
                url,
                user,
                password,
                null,
                null
        );

        Liquibase liquibase = new Liquibase(
                "liquibase/db/master.xml",
                new ClassLoaderResourceAccessor(),
                database
        );

        logger.info("Running liquibase migrations....");
        liquibase.update(new Contexts(), new LabelExpression());
        logger.info("Liquibase migrations SUCCESS.");
    }
}
