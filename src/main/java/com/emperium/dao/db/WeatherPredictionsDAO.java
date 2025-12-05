package com.emperium.dao.db;

import java.util.List;

public interface WeatherPredictionsDAO {

    List<Object[]> getCityPredictions(String city);

    List<Object[]> getCityDailyPredictions(String city, String date);

    List<Object[]> getCityPredictionsPerHour(String city, String date, String time);
}
