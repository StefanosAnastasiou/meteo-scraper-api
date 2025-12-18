package com.emperium;

import com.emperium.config.LiquibaseRunner;
import com.emperium.luceneindex.Indexer;
import com.emperium.scraper.MeteoScraper;
import liquibase.exception.LiquibaseException;
import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.quartz.SchedulerException;

import java.io.IOException;
import java.net.URI;

/**
 * @author Stefanos Anastasiou
 */
public class Server {

    private static final Logger logger = Logger.getLogger(Server.class);
    private static final MeteoScraper meteoScraper = new MeteoScraper();
    private static final Indexer indexer = new Indexer();

    private static final String BASE_URI = "http://0.0.0.0:8445/";
    private static final String RESOURCE_PACKAGE = "com.emperium";

    private static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages(RESOURCE_PACKAGE);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException, ParseException, SchedulerException, InterruptedException, LiquibaseException {
        // Delay application start up so that database container starts first. FIXME: move this solution to docker / docker compose
        Thread.sleep(5*1000);
        final HttpServer server = startServer();
        server.start();
        logger.info("Http server started...");

        indexer.createOrUpdateIndex();
        LiquibaseRunner.run();

        meteoScraper.init();
    }
}
