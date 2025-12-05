package com.emperium.dto.suggest;

/**
 * Data Transfer Object class. Used for JSON representation after Lucene index search
 */
public class CitySuggestionDTO {
    private String city;
    private String id;

    public String getCity() {
        return this.city;
    }

    public String getId() {
        return this.id;
    }

    public CitySuggestionDTO(Builder builder) {
        this.city = builder.city;
        this.id = builder.id;
    }

    public static class Builder {
        private String city;
        private String id;

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public CitySuggestionDTO build() {
            return new CitySuggestionDTO(this);
        }
    }
}
