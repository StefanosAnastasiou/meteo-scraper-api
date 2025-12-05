package com.emperium.service;

import com.emperium.dto.suggest.CitySuggestionDTO;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;

public interface CitySuggestionService {
    List<CitySuggestionDTO> getSuggestions(String city) throws IOException, ParseException;
}
