package com.emperium.dao.search;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

public interface CitySuggestionDAO {

   TopDocs getSuggestions(String city) throws IOException, ParseException;
}
