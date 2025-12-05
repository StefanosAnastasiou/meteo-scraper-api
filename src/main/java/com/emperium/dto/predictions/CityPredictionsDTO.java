package com.emperium.dto.predictions;

import java.util.List;

/**
 * Data Transfer Object class that represent the whole weather forecast prediction
 */
public class CityPredictionsDTO {
    private String city;
    private List<DatePredictionsDTO> data;

    public String getCity() {
        return city;
    }

    public List<DatePredictionsDTO> getData() {
        return data;
    }

    public CityPredictionsDTO(Builder builder) {
        this.city = builder.city;
        this.data = builder.date;
    }

    public static class Builder {
        private String city;
        private List<DatePredictionsDTO> date;

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setDate(List<DatePredictionsDTO> date) {
            this.date = date;
            return this;
        }

        public CityPredictionsDTO build() {
            return new CityPredictionsDTO(this);
        }
    }
}
