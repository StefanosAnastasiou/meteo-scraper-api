package com.emperium.dao.db;

import java.util.List;

public interface CityPredictionsDAO {
    List<Object[]> getCityDailyPredictions(String city, String date);

    List<Object[]> getCityPredictionsPerHour(String city, String date, String time);

    List<Object[]> getCityPredictions(String city);
}
