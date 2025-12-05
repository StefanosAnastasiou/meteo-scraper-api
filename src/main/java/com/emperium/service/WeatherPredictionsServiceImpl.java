package com.emperium.service;

import com.emperium.dao.db.WeatherPredictionsDAO;
import com.emperium.dao.db.WeatherPredictionsDAOImpl;
import com.emperium.dto.predictions.DatePredictionsDTO;
import com.emperium.dto.predictions.CityPredictionsDTO;
import com.emperium.dto.predictions.PredictionsDTO;
import com.emperium.mappers.Mapper;

import java.sql.Time;
import java.util.List;

/**
 * Service class for fetching weather predictions
 */
public class WeatherPredictionsServiceImpl implements WeatherPredictionsService {

    private final WeatherPredictionsDAO weatherPredictionsDAO;

    public WeatherPredictionsServiceImpl() {
        this.weatherPredictionsDAO = new WeatherPredictionsDAOImpl();
    }

    /**
     * Returns the weather predictions per city
     *
     * @param city the city
     * @return the {@link CityPredictionsDTO}
     */
    @Override
    public CityPredictionsDTO getCityPredictions(String city) {
        List<Object[]> result = weatherPredictionsDAO.getCityPredictions(city);
        List<DatePredictionsDTO> datePredictionsDTOS = Mapper.queryResultToDto(result);

        /*
         * The query results in the List<Object[]> carry the same city. So it is safe to get the first
         * - or even any other element that holds city value in the array results
         */
        String predictionCity = (String) result.get(0)[0];

        CityPredictionsDTO cityPredictionsDTO = new CityPredictionsDTO.Builder()
                .setCity(predictionCity)
                .setDate(datePredictionsDTOS)
                .build();

        if (!result.isEmpty()) return cityPredictionsDTO;

        return new CityPredictionsDTO.Builder().build();

    }

    /**
     * Returns the weather predictions per city, on a specified date
     *
     * @param city the city for which the predictions are queried
     * @param date the date of the predictions in the specified city
     * @return the {@link CityPredictionsDTO}
     */
    @Override
    public CityPredictionsDTO getCityDailyPredictions(String city, String date) {
        List<Object[]> result = weatherPredictionsDAO.getCityDailyPredictions(city, date);
        List<DatePredictionsDTO> datePredictionsDTOS = Mapper.queryResultToDto(result);

        /* All these query results carry the same city so it is safe to get the first - or even any other element
         * in the array results
         */
        String predictionCity = (String) result.get(0)[0];

        CityPredictionsDTO cityPredictionsDTO = new CityPredictionsDTO.Builder()
                .setCity(predictionCity)
                .setDate(datePredictionsDTOS)
                .build();

        if (!result.isEmpty()) return cityPredictionsDTO;

        return cityPredictionsDTO;
    }

    /**
     * Returns the weather predictions from the database per city, on a specified date and time
     *
     * @param city the city for which the predictions are queried
     * @param date the date of the predictions needed in the previously specified city
     * @param time the time of the predictions needed in the previously specified date
     * @return the {@link PredictionsDTO}
     */
    @Override
    public PredictionsDTO getCityPredictionsPerHour(String city, String date, String time) {
        List<Object[]> result = weatherPredictionsDAO.getCityPredictionsPerHour(city, date, time);

        return result.stream()
                .map(obj -> new PredictionsDTO.Builder()
                        .setTime(((Time) obj[2]).toLocalTime())
                        .setTemperature((Integer) obj[3])
                        .setHumidity((Integer) obj[5])
                        .setWind((String) obj[4])
                        .setPhenomenon((String) obj[6])
                        .build()
                )
                .findFirst()
                .orElse(null);
    }
}
