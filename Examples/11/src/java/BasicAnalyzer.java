import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

public class BasicAnalyzer
        extends AnalyzerWrapper {
    
    private final Analyzer defaultAnalyzer;
    private final Map<String, Analyzer> fieldAnalyzers;

    public BasicAnalyzer() {
        super(AnalyzerWrapper.PER_FIELD_REUSE_STRATEGY);
        
        defaultAnalyzer = new EnglishAnalyzer();
        fieldAnalyzers = new HashMap<>();
        
        fieldAnalyzers.put("asin", new WhitespaceLowerCaseAnalyzer());
        fieldAnalyzers.put(Example11.FIELD_TITLE + Example11.IndexFieldVariationSeparator + Example11.IndexFieldVariation_Dictionary, new DictionaryAnalyzer());
    }

    @Override
    protected Analyzer getWrappedAnalyzer(String fieldName) {
        Analyzer analyzer = fieldAnalyzers.get(fieldName);
        
        return (analyzer != null) ? analyzer : defaultAnalyzer;
    }

    @Override
    protected TokenStreamComponents wrapComponents(String fieldName, TokenStreamComponents components) {
        return components;
    }
    
}
