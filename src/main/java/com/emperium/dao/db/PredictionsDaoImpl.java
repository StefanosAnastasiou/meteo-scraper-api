package com.emperium.dao.db;

import com.emperium.model.Predictions;
import com.emperium.utils.SQL;

import java.util.List;


public class PredictionsDaoImpl implements PredictionsDAO {

    @Override
    public boolean isPredictionsSet(int day_id) {
            return SQL.getInstance().isDailyPredictionsSet(day_id);
    }

    @Override
    public void checkAndUpdateDailyPredictions(int day_id, List<Predictions> predictions) {
             SQL.getInstance().checkAndUpdateDailyPredictions(day_id, predictions);
    }

    @Override
    public void setDailyPredictions(List<Predictions> predictions, int day_id) {
        SQL.getInstance().selectDailyPredictions(predictions, day_id);
    }

    @Override
    public void deleteByCityId(int city_id) {
        SQL.getInstance().deletePredictionsByDayId(city_id);
    }
}