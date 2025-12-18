package com.emperium.dto.predictions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object class that represent the whole weather forecast prediction
 */
@Getter
@Setter
@Builder
public class CityPredictionsDTO {
    private String city;
    private List<DatePredictionsDTO> data;
}
