package com.emperium.dto.predictions;

import java.time.LocalTime;

/**
 * Data Transfer Object Class. Represents the predictions JSON section
 */
public class PredictionsDTO {
    private LocalTime time;
    private Integer temperature;
    private Integer humidity;
    private String wind;
    private String phenomeno;

    public PredictionsDTO(Builder builder) {
        this.time = builder.time;
        this.temperature = builder.temperature;
        this.humidity = builder.humidity;
        this.wind = builder.wind;
        this.phenomeno = builder.phenomeno;
    }

    public LocalTime getTime() {
        return time;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public String getWind() {
        return wind;
    }

    public String getPhenomeno() {
        return phenomeno;
    }

    public static class Builder {
        private LocalTime time;
        private Integer temperature;
        private Integer humidity;
        private String wind;
        private String phenomeno;

        public Builder setTime(LocalTime time) {
            this.time = time;
            return this;
        }
        public Builder setTemperature(Integer temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder setHumidity(Integer humidity) {
            this.humidity = humidity;
            return this;
        }

        public Builder setWind(String wind) {
            this.wind = wind;
            return this;
        }

        public Builder setPhenomenon(String phenomeno) {
            this.phenomeno = phenomeno;
            return this;
        }
        public PredictionsDTO build() {
            return new PredictionsDTO(this);
        }
    }
}
