
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public class Example {
    
    private static final String FIELD_ASIN = "asin";
    private static final String FIELD_TITLE = "title";
    
    public static void main(String args[])
            throws Exception {
        AnalyzerExamples("This is a test. How about that?! Huh?");
        AnalyzerExamples("セックス・アンド・ザ・シティ2 ［ザ・ムービー］ [DVD]");
    }
    
    public static void AnalyzerExamples(String input) 
            throws IOException {
        System.out.println("input = \"" + input + "\"");
        System.out.println();

        BasicAnalyzerExample(input, FIELD_TITLE, FIELD_ASIN);
        JapaneseAnalyzerExample(input, FIELD_TITLE, FIELD_ASIN);
    }
    
    public static void BasicAnalyzerExample(String input, String... fields) 
            throws IOException {
        for(String field : fields) {
            try (Analyzer analyzer = new BasicAnalyzer()) {
                ExecuteAnalyzerExample(field, input, analyzer);
            }
        }
    }
    
    public static void JapaneseAnalyzerExample(String input, String... fields) 
            throws IOException {
        for(String field : fields) {
            try (Analyzer analyzer = new JapaneseAnalyzer()) {
                ExecuteAnalyzerExample(field, input, analyzer);
            }
        }
    }
    
    public static List<String> ExecuteAnalyzerExample(String field, String input, Analyzer analyzer) 
            throws IOException {
        System.out.println("Field = " + field);
        System.out.println("  Analyzer = " + analyzer.getClass().getSimpleName());
        
        TokenStream ts = analyzer.tokenStream(field, input);
        
        CharTermAttribute charTermAttribute = ts.getAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAttribute = ts.getAttribute(OffsetAttribute.class);
        PositionIncrementAttribute positionIncrementAttribute = ts.getAttribute(PositionIncrementAttribute.class);
        PositionLengthAttribute positionLengthAttribute = ts.getAttribute(PositionLengthAttribute.class);
        TypeAttribute typeAttribute = ts.getAttribute(TypeAttribute.class);
        
        List<String> tokens = new ArrayList<>();
        ts.reset();

        while (ts.incrementToken()) {
            tokens.add(charTermAttribute.toString());
          
            System.out.println("    type = " + typeAttribute.type() + ", startOffset = " + offsetAttribute.startOffset() + ", endOffset = " + offsetAttribute.endOffset() + ": \"" + charTermAttribute.toString() + "\", positionIncrement = " + positionIncrementAttribute.getPositionIncrement() + ", positionLength = " + positionLengthAttribute.getPositionLength());
        }
        ts.end();

        System.out.println();
        
        return tokens;
    }
}
