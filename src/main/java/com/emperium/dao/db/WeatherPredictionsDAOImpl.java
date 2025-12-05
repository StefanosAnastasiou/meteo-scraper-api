package com.emperium.dao.db;

import java.util.*;

public class WeatherPredictionsDAOImpl implements WeatherPredictionsDAO {
    private final CityPredictionsDAO predictionsDb = new CityPredictionsDAOImpl();

    @Override
    public List<Object[]> getCityPredictions(String city) {
        return predictionsDb.getCityPredictions(city);
    }

    @Override
    public List<Object[]> getCityDailyPredictions(String city, String date) {
        return predictionsDb.getCityDailyPredictions(city, date);
    }

    @Override
    public List<Object[]> getCityPredictionsPerHour(String city, String day, String time) {
        return predictionsDb.getCityPredictionsPerHour(city, day, time);
    }
}
