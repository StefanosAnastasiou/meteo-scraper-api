package com.emperium.dto.predictions;

import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object class
 */
public class DatePredictionsDTO {
    private Date date;
    private List<PredictionsDTO> predictions;

    public DatePredictionsDTO(Builder builder) {
        this.date = builder.date;
        this.predictions = builder.predictions;
    }

    public Date getDate() {
        return date;
    }

    public List<PredictionsDTO> getPredictions() {
        return predictions;
    }

    public static class Builder {
        private Date date;
        private List<PredictionsDTO> predictions;

        public Builder setDate(Date date) {
            this.date = date;
            return this;
        }

        public Builder setPredictions(List<PredictionsDTO> predictions) {
            this.predictions = predictions;
            return this;
        }

        public DatePredictionsDTO build() {
            return new DatePredictionsDTO(this);
        }
    }
}
