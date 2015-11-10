import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public class Example {
    // Based on: http://lucidbox.com/?p=26
    public static void main(String args[])
            throws Exception {
        final String input = "This is a test. How about that?! Huh?";

        TokenizerExamples(input);
    }
    
    public static void TokenizerExamples(String input) 
            throws IOException {
        StandardTokenizerExample(input);
        WhitespaceLowerCaseAnalyzerExample(input);
    }
    
    public static void StandardTokenizerExample(String input) 
            throws IOException {
        try (Tokenizer tokenizer = new StandardTokenizer()) {
            ExecuteTokenizerExample(input, tokenizer);
        }
    }
    
    public static void WhitespaceLowerCaseAnalyzerExample(String input) 
            throws IOException {
        try (Tokenizer tokenizer = new WhitespaceTokenizer()) {
            ExecuteTokenizerExample(input, tokenizer);
        }
    }
    
    public static List<String> ExecuteTokenizerExample(String input, Tokenizer tokenizer) 
            throws IOException {
        System.out.println("Tokenizer = " + tokenizer.getClass().getSimpleName());
        
        tokenizer.setReader(new StringReader(input));

        CharTermAttribute charTermAttribute = tokenizer.getAttribute(CharTermAttribute.class);
        TypeAttribute typeAttribute = tokenizer.getAttribute(TypeAttribute.class);
        OffsetAttribute offsetAttribute = tokenizer.getAttribute(OffsetAttribute.class);

        List<String> tokens = new ArrayList<>();
        tokenizer.reset();

        while (tokenizer.incrementToken()) {
            tokens.add(charTermAttribute.toString());

            System.out.println("  type = " + typeAttribute.type() + ", startOffset = " + offsetAttribute.startOffset() + ", endOffset = " + offsetAttribute.endOffset() + ": \"" + charTermAttribute.toString() + "\"");
        }

        tokenizer.end();
        
        System.out.println();
        
        return tokens;
    }
}
