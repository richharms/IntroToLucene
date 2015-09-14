import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

public class AttributeQueryParser
        extends QueryParser {

    protected AttributeQueryParserUtils attributeQueryParserUtils;

    public AttributeQueryParser(final String field, final Analyzer a) {
        super(field, a);

        attributeQueryParserUtils = new AttributeQueryParserUtils();
    }
    
    @Override
    protected Query getFieldQuery(String field, String queryText, boolean quoted)
            throws ParseException {
        Query query = attributeQueryParserUtils.getFieldQuery(field, null, null, queryText, quoted);

        return query == null ? super.getFieldQuery(field, queryText, quoted) : query;
    }

    @Override
    protected Query newTermQuery(final Term term) {
        Query query = attributeQueryParserUtils.newTermQuery(term);

        return query == null ? super.newTermQuery(term) : query;
    }

    @Override
    protected Query getRangeQuery(final String field, final String min, final String max, final boolean startInclusive, final boolean endInclusive) {
        return newRangeQuery(field, min, max, startInclusive, endInclusive);
    }

    @Override
    protected Query newRangeQuery(final String field, final String min, final String max, final boolean startInclusive, final boolean endInclusive) {
        Query query = attributeQueryParserUtils.newRangeQuery(field, null, min, max, startInclusive, endInclusive);
        
        return query == null ? super.newRangeQuery(field, min, max, startInclusive, endInclusive) : query;
    }

}
