package com.emperium.dto.predictions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object class
 */
@Getter
@Setter
@Builder
public class DatePredictionsDTO {
    private Date date;
    private List<PredictionsDTO> predictions;
}
