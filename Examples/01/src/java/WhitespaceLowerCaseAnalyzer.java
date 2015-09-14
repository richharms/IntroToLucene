import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;

public class WhitespaceLowerCaseAnalyzer
        extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        final Tokenizer source = new WhitespaceTokenizer();
        final TokenStream result = new LowerCaseFilter(source);

        return new TokenStreamComponents(source, result);
    }

}
