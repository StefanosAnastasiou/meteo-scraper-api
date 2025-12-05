package com.emperium.dao.db;

import com.emperium.model.Predictions;
import com.emperium.utils.SQL;

import java.util.List;


public class PredictionsDaoImpl implements PredictionsDAO {

    @Override
    public boolean isPredictionsSet(int day_id) {
            return SQL.getInstance().dailyMeasurementsAreSet(day_id);
    }

    @Override
    public void checkAndUpdateDailyPredictions(int day_id, List<Predictions> predictions) {
             SQL.getInstance().checkAndUpdateDailyMeasurement(day_id, predictions);
    }

    @Override
    public void setDailyPredictions(List<Predictions> predictions, int day_id) {
        SQL.getInstance().setDailyMeasurement(predictions, day_id);
    }

    @Override
    public void deleteByCityId(int city_id) {
        SQL.getInstance().deleteMeasurementsByDayId(city_id);
    }
}