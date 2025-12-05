package com.emperium.dao.db;

import com.emperium.utils.SQL;

import java.util.List;

public class CityPredictionsDAOImpl implements CityPredictionsDAO {

    /**
     * Fetches weather predictions from the database for a specified city and day
     *
     * @param city the city
     * @param date the date
     * @return the query result {@link List}
     */
    @Override
    public List<Object[]> getCityDailyPredictions(String city, String date) {
        return SQL.getInstance().getCityDailyPredictions(city, date);
    }

    /**
     * Fetches weather from the database for a specified city, date and time
     *
     * @param city the city
     * @param date the date for which the weather predictions are queried
     * @param time the time for which the weather predictions are queried
     * @return the query result {@link List}
     */
    @Override
    public List<Object[]> getCityPredictionsPerHour(String city, String date, String time) {
        return SQL.getInstance().getCityPredictionsPerHour(city, date, time);
    }

    /**
     * Gets all the predictions for a specified city, from the time requested and onwards.
     *
     * @param city the city
     * @return the query result {@link List}
     */
    @Override
    public List<Object[]> getCityPredictions(String city) {
        return SQL.getInstance().getCityPredictions(city);
    }
}
