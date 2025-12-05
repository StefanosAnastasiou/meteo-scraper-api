package com.emperium.controller;

import com.emperium.service.CitySuggestionService;
import com.emperium.service.CitySuggestionServiceImpl;
import com.emperium.dto.suggest.CitySuggestionDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import jakarta.ws.rs.core.Response;
import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Controller class that returns suggestion cities for specific key input
 */
@Path("suggest")
public class CitySuggestionController {
    private static final Logger logger = LoggerFactory.getLogger(WeatherPredictionController.class);
    private CitySuggestionService citySuggestionService = new CitySuggestionServiceImpl();

    /**
     * Controller method that returns suggestions from Lucene according to parameter input
     *
     * @param input the input character(s) of the city to be searched
     * @return a {@link Response}
     * @throws IOException thrown
     * @throws ParseException thrown
     */
    @GET
    @Path("/{input}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCitiesSuggestions(@PathParam("input") String input) throws IOException, ParseException {
        logger.info("Getting city suggestions for input: " + input);
        List<CitySuggestionDTO> suggestionList =  citySuggestionService.getSuggestions(input);

        if (!suggestionList.isEmpty()) {
            return Response
                    .status(Response.Status.OK.getStatusCode())
                    .entity(suggestionList)
                    .build();
        }
        logger.info("No city suggestions found for input: " + input);

        return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
    }
}
