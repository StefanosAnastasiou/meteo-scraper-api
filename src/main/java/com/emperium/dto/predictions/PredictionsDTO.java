package com.emperium.dto.predictions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

/**
 * Data Transfer Object Class. Represents the predictions JSON section
 */
@Getter
@Setter
@Builder
public class PredictionsDTO {
    private LocalTime time;
    private Integer temperature;
    private Integer humidity;
    private String wind;
    private String phenomeno;
}
