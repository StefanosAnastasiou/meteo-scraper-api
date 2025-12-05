package com.emperium.mappers;

import com.emperium.dto.predictions.DatePredictionsDTO;
import com.emperium.dto.predictions.PredictionsDTO;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper Class that maps DTO to Model
 */
public class Mapper {

    private Mapper() {
        // Do not instantiate
    }

    /**
     * The query results returned from the database and are input in the parameter, are in the form of
     * List<["2025-12-12", "02:00", "15", "20", "75", "'event name'"],
     * ["2025-12-12", "05:00", "13", "18", "62", "'event name'"],
     * ["2025-12-12", "08:00", "16", "20", "60", "'event name'"]>
     * This method maps the result to a {@link DatePredictionsDTO} by extracting the unique {@link DatePredictionsDTO#date}
     * and mapping the {@link DatePredictionsDTO#predictions} in the equivalent array element of the result List
     * to that specific date
     *
     * @param queryResult the query result
     * @return a List of {@link DatePredictionsDTO}
     */
    public static List<DatePredictionsDTO> queryResultToDto(List<Object[]> queryResult) {
        List<Predictions> predictionsList = new ArrayList<>();

        /* Map the result to the inner class Predictions and thus have the List<Object[]>
        *  transformed to class. Then add them to list */
        queryResult.forEach(obj -> predictionsList.add(
                new Predictions.Builder()
                        .setDate((Date) obj[1])
                        .setTime(((Time) obj[2]).toLocalTime())
                        .setTemperature((Integer) obj[3])
                        .setWind((String) obj[4])
                        .setHumidity((Integer) obj[5])
                        .setPhenomenon((String) obj[6])
                        .build()
        ));

        /* Map previously create List (predictionsList) to predictions per Date in this temporary Map */
        Map<Date, List<Predictions>> theMapping = predictionsList
                .stream()
                .collect(Collectors.groupingBy(Predictions::getDate));

        List<DatePredictionsDTO> datePredictionsDtoList = new ArrayList<>();

        /* Finally map the previously create Map to DTO */
        for (Map.Entry<Date, List<Predictions>> entry : theMapping.entrySet()) {
            List<PredictionsDTO> measurements = new ArrayList<>();
            entry.getValue().forEach(obj -> measurements.add(
                    new PredictionsDTO.Builder()
                            .setTime(obj.getTime())
                            .setTemperature(obj.getTemperature())
                            .setWind(obj.getWind())
                            .setHumidity(obj.getHumidity())
                            .setPhenomenon(obj.getPhenomenon())
                            .build()
            ));

            datePredictionsDtoList.add(
                    new DatePredictionsDTO.Builder()
                            .setDate(entry.getKey())
                            .setPredictions(measurements)
                            .build()
            );
        }

        return datePredictionsDtoList;
    }

    /**
     * Inner class used for the transformation of the result to an intermediate DTO Object
     *
     */
    private static class Predictions {
        private Date date;
        private LocalTime time;
        private Integer temperature;
        private String wind;
        private Integer humidity;
        private String phenomenon;

        public Predictions(Builder builder) {
            this.date = builder.date;
            this.time = builder.time;
            this.temperature = builder.temperature;
            this.wind = builder.wind;
            this.humidity = builder.humidity;
            this.phenomenon = builder.phenomenon;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public LocalTime getTime() {
            return time;
        }

        public void setTime(LocalTime time) {
            this.time = time;
        }

        public Integer getTemperature() {
            return temperature;
        }

        public void setTemperature(Integer temperature) {
            this.temperature = temperature;
        }

        public String getWind() {
            return wind;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }

        public Integer getHumidity() {
            return humidity;
        }

        public void setHumidity(Integer humidity) {
            this.humidity = humidity;
        }

        public String getPhenomenon() {
            return phenomenon;
        }

        public void setPhenomenon(String phenomenon) {
            this.phenomenon = phenomenon;
        }

        public static class Builder {
            private Date date;
            private LocalTime time;
            private Integer temperature;
            private String wind;
            private Integer humidity;
            private String phenomenon;

            private Builder setDate(Date date) {
                this.date = date;
                return this;
            }

            public Builder setTime(LocalTime time) {
                this.time = time;
                return this;
            }

            public Builder setTemperature(Integer temperature) {
                this.temperature = temperature;
                return this;
            }

            public Builder setWind(String wind) {
                this.wind = wind;
                return this;
            }

            public Builder setHumidity(Integer humidity) {
                this.humidity = humidity;
                return this;
            }

            public Builder setPhenomenon(String phenomenon) {
                this.phenomenon = phenomenon;
                return this;
            }

            public Predictions build() {
                return new Predictions(this);
            }
        }
    }
}
