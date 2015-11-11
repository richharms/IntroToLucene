import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.spell.DirectSpellChecker;
import org.apache.lucene.search.spell.SuggestWord;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Example11 {
    
    public static final String INDEX_DIRECTORY = "index";
    
    public static final String FIELD_IDENTIFIER = "identifier";
    public static final String FIELD_ASIN = "asin";
    public static final String FIELD_TITLE = "title";
    
    public static final String IndexFieldVariationSeparator = ":";

    public static final String IndexFieldVariation_Dictionary = "dictionary";
    
    public static void main(String args[])
            throws Exception {
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        Analyzer analyzer = new BasicAnalyzer();
        
        writeIndex(directory, analyzer);
        getSuggestions(directory, analyzer, "pixals pexals");
        getSuggestions(directory, analyzer, "alohe aleha alehe");
        getSuggestions(directory, analyzer, "chipter chaptor choptor");
        getSuggestions(directory, analyzer, "blue");
    }
    
    public static void writeIndex(Directory directory, Analyzer analyzer) 
            throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        
        // Alternative is IndexWriterConfig.OpenMode.APPEND.
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        
        try (IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig)) {
            addDocument(indexWriter, "1", "B011KME78M", "Tremors 5: Bloodlines (Blu-ray + DVD + DIGITAL HD)");
            addDocument(indexWriter, "2", "B00YHR5UW0", "Aloha [Blu-ray]");
            addDocument(indexWriter, "3", "B012DWS874", "Pixels (3D Blu-ray + Blu-ray + UltraViolet)");
            addDocument(indexWriter, "4", "B00YT8SCSG", "Insidious: Chapter 3 (Blu-ray + Ultraviolet)");
            addDocument(indexWriter, "5", "B00VGQKBQ0", "The Walking Dead: Season 5 [Blu-ray]");
        }
    }
    
    private static void addDocument(IndexWriter indexWriter, String identifier, String asin, String title)
            throws IOException {
        Document document = new Document();
        
        document.add(new Field(FIELD_IDENTIFIER, identifier, FieldTypes.STORED_NOT_TOKENIZED));
        document.add(new Field(FIELD_ASIN, asin, FieldTypes.NOT_STORED_TOKENIZED));
        document.add(new Field(FIELD_TITLE, title, FieldTypes.NOT_STORED_TOKENIZED));
        document.add(new Field(FIELD_TITLE + IndexFieldVariationSeparator + IndexFieldVariation_Dictionary, title, FieldTypes.NOT_STORED_TOKENIZED));
        
        indexWriter.addDocument(document);
    }
    
    public static void getSuggestions(Directory directory, Analyzer analyzer, String q)
            throws ParseException, IOException {
        System.out.println("suggestions for: " + q);
        
        List<String> tokens = getTokens(q);
        DirectSpellChecker directSpellChecker = new DirectSpellChecker();

        IndexReader indexReader = DirectoryReader.open(directory);
        
        for(String word : tokens) {
            System.out.println("  word: " + word);
            
            Term term = new Term(FIELD_TITLE + IndexFieldVariationSeparator + IndexFieldVariation_Dictionary, word);
            SuggestWord[] suggestWords = directSpellChecker.suggestSimilar(term, 5, indexReader);
            
            if(suggestWords.length == 0) {
                System.out.println("    no suggestions.");
            } else {
                for(SuggestWord suggestWord : suggestWords) {
                    System.out.println("    " + suggestWord.string + ", " + suggestWord.freq + ", " + suggestWord.score);
                }
            }
        }
        
        System.out.println();
    }
    
    public static List<String> getTokens(String input) 
            throws IOException {
        Analyzer analyzer = new BasicAnalyzer();
        TokenStream ts = analyzer.tokenStream(FIELD_TITLE + IndexFieldVariationSeparator + IndexFieldVariation_Dictionary, input);
        
        CharTermAttribute charTermAttribute = ts.getAttribute(CharTermAttribute.class);
        
        List<String> tokens = new ArrayList<>();
        ts.reset();

        while (ts.incrementToken()) {
            tokens.add(charTermAttribute.toString());
        }
        ts.end();
        
        return tokens;
    }
    
}
