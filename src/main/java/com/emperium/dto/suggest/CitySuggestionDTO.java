package com.emperium.dto.suggest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object class. Used for JSON representation on searching Lucene
 */
@Getter
@Setter
@Builder
public class CitySuggestionDTO {
    private String city;
    private String id;
}
