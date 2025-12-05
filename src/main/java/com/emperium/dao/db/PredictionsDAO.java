package com.emperium.dao.db;

import com.emperium.model.Predictions;

import java.util.List;

public interface PredictionsDAO {

    boolean isPredictionsSet(int day_id);

    void checkAndUpdateDailyPredictions(int day_id, List<Predictions> predictions);

    void setDailyPredictions(List<Predictions> predictions, int day_id);

    void deleteByCityId(int city_id);
}
