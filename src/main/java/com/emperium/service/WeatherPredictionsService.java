package com.emperium.service;

import com.emperium.dto.predictions.CityPredictionsDTO;
import com.emperium.dto.predictions.PredictionsDTO;

public interface WeatherPredictionsService {
    CityPredictionsDTO getCityPredictions(String city);

    CityPredictionsDTO getCityDailyPredictions(String city, String date);

    PredictionsDTO getCityPredictionsPerHour(String city, String date, String time);
}
