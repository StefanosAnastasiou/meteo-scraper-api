package com.emperium.service;

import com.emperium.dao.search.CitySuggestionDAO;
import com.emperium.dao.search.CitySuggestionDAOImpl;
import com.emperium.dto.suggest.CitySuggestionDTO;
import com.emperium.luceneindex.Utils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class that queries Lucene
 */
public class CitySuggestionServiceImpl implements CitySuggestionService {

    private final CitySuggestionDAO citySuggestionDAO;

    public CitySuggestionServiceImpl() {
        this.citySuggestionDAO = new CitySuggestionDAOImpl();
    }

    /**
     * Queries Lucene and returns city suggestions according to input
     *
     * @param city the city to search for suggestions in Lucene
     * @return the {@link TopDocs} of query returned
     * @throws IOException    thrown
     * @throws ParseException thrown
     */
    @Override
    public List<CitySuggestionDTO> getSuggestions(String city) throws IOException, ParseException {
        List<CitySuggestionDTO> suggestions = new ArrayList<>();
        TopDocs topDocs = citySuggestionDAO.getSuggestions(city);
        IndexSearcher indexSearcher = Utils.getIndexSearcher();

        for (ScoreDoc sd : topDocs.scoreDocs) {
            Document d = indexSearcher.doc(sd.doc);
            suggestions.add(
                    CitySuggestionDTO.builder()
                            .city(String.format(d.get("city")))
                            .id(String.format(d.get("id")))
                            .build()
            );
        }
        return suggestions;
    }
}
