package com.emperium.luceneindex;

import com.emperium.utils.Mappings;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Class responsible for indexing cities in Lucene
 */
public class Indexer {

    private final Logger logger = Logger.getLogger(Indexer.class);
    private static final String CITY = "city";
    private static final String ID = "id";

    public void createOrUpdateIndex() throws IOException, ParseException {
        if(isIndexSet()) {
            logger.info(">>>> Cities index found.");
            searchDocuments();
        } else {
            createIndex();
        }
    }

    private boolean isIndexSet() throws IOException {
        try(Directory citiesIndex = FSDirectory.open(Paths.get(Utils.INDEX_PATH))) {
            return DirectoryReader.indexExists(citiesIndex);
        }
    }

    private void searchDocuments() throws IOException, ParseException {
        IndexSearcher searcher = Utils.getIndexSearcher();

        for (Map.Entry<Integer, String> entry : Mappings.cityMappings.entrySet()) {
            TopDocs docs = searchByCity(entry.getValue(), searcher);

            if (docs.scoreDocs.length == 0) {
                IndexWriter writer = Utils.getIndexWriter();
                Document document = updateIndex(CITY, entry.getValue(), ID, String.valueOf(entry.getKey()));
                writer.addDocument(document);
                writer.close();
            }
        }
    }

    private void createIndex() throws IOException {
        IndexWriter indexWriter = Utils.getIndexWriter();

        for(Map.Entry<Integer, String> city : Mappings.cityMappings.entrySet()) {
            Document document = createDocument(city.getValue(), city.getKey());
            indexWriter.addDocument(document);
        }

        indexWriter.close();
        logger.info(">>>>> Cities index created");
    }

    private TopDocs searchByCity(String city, IndexSearcher searcher) throws ParseException, IOException {
        QueryParser qp = new QueryParser(CITY, new SimpleAnalyzer());
        Query query = qp.parse(QueryParserBase.escape(city));

        TopDocs docs = searcher.search(query, 10);

        return docs;
    }

    private Document updateIndex(String cityField, String cityValue, String idField, String idValue) {
        Document document = new Document();

        document.add(new TextField(cityField, cityValue, Field.Store.YES));
        document.add(new TextField(idField, idValue, Field.Store.YES));

        logger.info(">>>> New document added: city: " + cityValue + " id: " + idValue);
        return document;
    }

    /** Use TextField so that values can be analyzed. */
    private Document createDocument(String city, Integer id) {
        Document document = new Document();
        document.add(new TextField(CITY, city, Field.Store.YES));
        document.add(new TextField(ID, String.valueOf(id), Field.Store.YES));

        return document;
    }
}