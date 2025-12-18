package com.emperium.controller;

import com.emperium.errors.ErrorMessage;
import com.emperium.service.WeatherPredictionsService;
import com.emperium.service.WeatherPredictionsServiceImpl;
import com.emperium.dto.predictions.CityPredictionsDTO;
import com.emperium.dto.predictions.PredictionsDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.log4j.Logger;

/**
 * Controller class that fetches weather predictions per city
 * @author Stefanos Anastasiou
 */
@Path("predictions")
public class WeatherPredictionController {
    private static final Logger logger = Logger.getLogger(WeatherPredictionController.class);
    private WeatherPredictionsService predictionsService = new WeatherPredictionsServiceImpl();

    /**
     * Controller method that returns the weather predictions for a specified city.
     *
     * @param city The requested city
     * @return the {@link Response}
     */
    @GET
    @Path("/{city}")
    public Response cityPredictions(@PathParam("city") String city) {
        logger.info("Fetching predictions for " + city);
        CityPredictionsDTO result = predictionsService.getCityPredictions(city);

        if (result.getCity() == null || result.getData() == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Not found"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        return Response.ok(result, MediaType.APPLICATION_JSON)
                .entity(result)
                .build();
    }

    /**
     * Controller method that returns the weather predictions for a city for a specified day
     *
     * @param city the requested city
     * @param date the requested date
     * @return the {@link Response}
     */
    @GET
    @Path("/{city}/{day}")
    public Response cityDailyPrediction(@PathParam("city") String city, @PathParam("day") String date) {
        logger.info("Fetching daily predictions for  " + city );
        CityPredictionsDTO result = predictionsService.getCityDailyPredictions(city, date);
        if (result.getCity() == null || result.getData() == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Not found"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response.ok(result, MediaType.APPLICATION_JSON)
                .entity(result)
                .build();
    }

    /**
     * Controller method that returns the weather predictions for a city on a specified day and time.
     *
     * @param city the requested city
     * @param day the requested day
     * @param time the requested time of the days
     * @return the {@link Response}
     */
    @GET
    @Path("{city}/{day}/{time}")
    public Response cityHourPrediction(@PathParam("city") String city, @PathParam("day") String day, @PathParam("time") String time) {
        logger.info("Fetching predictions for " + city + " on: " + day + " at: " + time);
        PredictionsDTO result = predictionsService.getCityPredictionsPerHour(city, day, time);

        if(result == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Not found"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response.ok(result, MediaType.APPLICATION_JSON)
                .entity(result)
                .build();
    }
}
