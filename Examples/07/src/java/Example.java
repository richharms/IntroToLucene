import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PositiveScoresOnlyCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Example {
    
    private static final String INDEX_DIRECTORY = "index";
    
    private static final String FIELD_IDENTIFIER = "identifier";
    private static final String FIELD_ASIN = "asin";
    private static final String FIELD_TITLE = "title";
    
    public static void main(String args[])
            throws Exception {
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        Analyzer analyzer = new BasicAnalyzer();
        
        writeIndex(directory, analyzer);
        searchIndex(directory, analyzer, QueryParser.Operator.AND, "Insidious Year 3"); // Changed
        searchIndex(directory, analyzer, QueryParser.Operator.OR, "Insidious Year 3"); // Changed
    }
    
    public static void writeIndex(Directory directory, Analyzer analyzer) 
            throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        
        try (IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig)) {
            addDocument(indexWriter, "1", "B011KME78M", "Tremors 5: Bloodlines (Blu-ray + DVD + DIGITAL HD)");
            addDocument(indexWriter, "2", "B00YHR5UW0", "Aloha [Blu-ray]");
            addDocument(indexWriter, "3", "B012DWS874", "Pixels (3D Blu-ray + Blu-ray + UltraViolet)");
            addDocument(indexWriter, "4", "B00YT8SCSG", "Insidious: Chapter 4 (Blu-ray + Ultraviolet)");
            addDocument(indexWriter, "5", "B00VGQKBQ0", "The Walking Dead: Season 5 [Blu-ray]");
        }
    }
    
    private static void addDocument(IndexWriter indexWriter, String identifier, String asin, String title)
            throws IOException {
        Document document = new Document();
        
        document.add(new Field(FIELD_IDENTIFIER, identifier, FieldTypes.STORED_NOT_TOKENIZED));
        document.add(new Field(FIELD_ASIN, asin, FieldTypes.NOT_STORED_TOKENIZED));
        document.add(new Field(FIELD_TITLE, title, FieldTypes.NOT_STORED_TOKENIZED));
        
        indexWriter.addDocument(document);
    }
        
    protected static int getNumHits(IndexSearcher is, Query q)
            throws IOException {
        TotalHitCountCollector collector = new TotalHitCountCollector();
        
        is.search(q, collector);
        
        return collector.getTotalHits();
    }
    
    public static void searchIndex(Directory directory, Analyzer analyzer, QueryParser.Operator defaultOperator, String q) // Changed
            throws ParseException, IOException {
        System.out.println("Query: " + q);
        
        QueryParser queryParser = new QueryParser("title", analyzer); // Added
        
        queryParser.setDefaultOperator(defaultOperator); // Added

        Query query = queryParser.parse(q); // Changed
        System.out.println("Parsed query: " + query.toString());
        
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        int numHits = getNumHits(indexSearcher, query);
        
        if(numHits == 0) {
            System.out.println("    No matches.");
        } else {
            TopDocsCollector topDocsCollector = TopScoreDocCollector.create(numHits);
            Collector collector = new PositiveScoresOnlyCollector(topDocsCollector);

            indexSearcher.search(query, collector);

            ScoreDoc[] hits = topDocsCollector.topDocs().scoreDocs;
            final int hitCount = hits.length;
            
            if(hitCount > 0) {
                for(int i = 0 ; i < hitCount ; i++) {
                    System.out.println("    " + indexSearcher.doc(hits[i].doc).get(FIELD_IDENTIFIER));
                }
            }
        }
    }
    
}
