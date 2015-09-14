import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class Example {
    
    private static final String INDEX_DIRECTORY = "index";
    
    private static final String FIELD_IDENTIFIER = "identifier";
    private static final String FIELD_ASIN = "asin";
    private static final String FIELD_TITLE = "title";
    
    private static final String IndexFieldVariationSeparator = ":"; // Added
    
    private static final String IndexFieldVariation_Sortable = "sortable"; // Added
    
    public static void main(String args[])
            throws Exception {
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        Analyzer analyzer = new BasicAnalyzer();
        
        writeIndex(directory, analyzer);
        searchIndex(directory, analyzer, "Blu-ray");
    }
    
    public static void writeIndex(Directory directory, Analyzer analyzer) 
            throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        
        // Alternative is IndexWriterConfig.OpenMode.APPEND.
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        
        try (IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig)) {
            SortableDescriptionProducer sortableDescriptionProducer = SortableDescriptionProducerFactory.getInstance().getSortableDescriptionProducer("en"); // Added
            
            addDocument(indexWriter, "1", "B011KME78M", "Tremors 5: Bloodlines (Blu-ray + DVD + DIGITAL HD)", sortableDescriptionProducer);
            addDocument(indexWriter, "2", "B00YHR5UW0", "Aloha [Blu-ray]", sortableDescriptionProducer);
            addDocument(indexWriter, "3", "B012DWS874", "Pixels (3D Blu-ray + Blu-ray + UltraViolet)", sortableDescriptionProducer);
            addDocument(indexWriter, "4", "B00YT8SCSG", "Insidious: Chapter 1 (Blu-ray + Ultraviolet)", sortableDescriptionProducer);
            addDocument(indexWriter, "5", "B00VGQKBQ0", "The Walking Dead: Season 5 [Blu-ray]", sortableDescriptionProducer);
            addDocument(indexWriter, "6", "B00YT8SCSH", "Insidious: Chapter 10 (Blu-ray + Ultraviolet)", sortableDescriptionProducer);
            addDocument(indexWriter, "7", "B00YT8SCSJ", "Insidious: Chapter 2 (Blu-ray + Ultraviolet)", sortableDescriptionProducer);
        }
    }
    
    
    private static void addDocument(IndexWriter indexWriter, String identifier, String asin, String title, SortableDescriptionProducer sortableDescriptionProducer)
            throws IOException {
        Document document = new Document();
        
        document.add(new Field(FIELD_IDENTIFIER, identifier.toLowerCase(), FieldTypes.STORED_NOT_TOKENIZED));
        document.add(new Field(FIELD_ASIN, asin.toLowerCase(), FieldTypes.STORED_NOT_TOKENIZED));
        document.add(new Field(FIELD_TITLE, title.toLowerCase(), FieldTypes.NOT_STORED_TOKENIZED));
        document.add(new SortedDocValuesField(FIELD_TITLE + IndexFieldVariationSeparator + IndexFieldVariation_Sortable, new BytesRef(sortableDescriptionProducer.getSortableDescription(title)))); // Changed
        
        indexWriter.addDocument(document);
    }
    
    protected static int getNumHits(IndexSearcher is, Query q)
            throws IOException {
        TotalHitCountCollector collector = new TotalHitCountCollector();
        
        is.search(q, collector);
        
        return collector.getTotalHits();
    }
    
    private static Sort getSort() {
        SortField[] sortFields = new SortField[]{new SortField(FIELD_TITLE + IndexFieldVariationSeparator + IndexFieldVariation_Sortable, SortField.Type.STRING, false)};
        
        return new Sort(sortFields);
    }
    
    public static void searchIndex(Directory directory, Analyzer analyzer, String q)
            throws ParseException, IOException {
        System.out.println("Query: " + q);
        
        Query query = new QueryParser("title", analyzer).parse(q);
        System.out.println("Parsed query: " + query.toString());
        
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        int numHits = getNumHits(indexSearcher, query);
        
        if(numHits == 0) {
            System.out.println("    No matches.");
        } else {
            TopFieldDocs topFieldDocs = indexSearcher.search(query, numHits, getSort());
            ScoreDoc[] hits = topFieldDocs.scoreDocs;
            final int hitCount = hits.length;
            
            if(hitCount > 0) {
                for(int i = 0 ; i < hitCount ; i++) {
                    System.out.println("    " + indexSearcher.doc(hits[i].doc).get(FIELD_IDENTIFIER));
                }
            }
        }
    }
    
}
