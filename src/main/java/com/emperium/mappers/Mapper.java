package com.emperium.mappers;

import com.emperium.dto.predictions.DatePredictionsDTO;
import com.emperium.dto.predictions.PredictionsDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper Class that maps DTO to Model
 * @author Stefanos Anastasiou
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
                Predictions.builder()
                        .date((Date) obj[1])
                        .time(((Time) obj[2]).toLocalTime())
                        .temperature((Integer) obj[3])
                        .wind((String) obj[4])
                        .humidity((Integer) obj[5])
                        .phenomenon((String) obj[6])
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
                    PredictionsDTO.builder()
                            .time(obj.getTime())
                            .temperature(obj.getTemperature())
                            .wind(obj.getWind())
                            .humidity(obj.getHumidity())
                            .phenomeno(obj.getPhenomenon())
                            .build()
            ));

            datePredictionsDtoList.add(
                    DatePredictionsDTO.builder()
                            .date(entry.getKey())
                            .predictions(measurements)
                            .build()
            );
        }

        return datePredictionsDtoList;
    }

    /**
     * Inner class used for the transformation of the result to an intermediate DTO Object
     */
    @Getter
    @Setter
    @Builder
    private static class Predictions {
        private Date date;
        private LocalTime time;
        private Integer temperature;
        private String wind;
        private Integer humidity;
        private String phenomenon;
    }
}
