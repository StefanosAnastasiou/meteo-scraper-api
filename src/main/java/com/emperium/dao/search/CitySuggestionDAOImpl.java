package com.emperium.dao.search;

import com.emperium.luceneindex.Utils;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

public class CitySuggestionDAOImpl implements CitySuggestionDAO {

    @Override
    public TopDocs getSuggestions(String city) throws IOException, ParseException {
        return  searchByWildCard(city);
    }

    private TopDocs searchByWildCard(String city) throws IOException, ParseException {
        IndexSearcher searcher = Utils.getIndexSearcher();
        QueryParser qp = new QueryParser("city", new SimpleAnalyzer());
        qp.setAllowLeadingWildcard(true);

        Query query = qp.parse("*" + city + "*");
        TopDocs hits = searcher.search(query, 100);

        return hits;
    }
}
