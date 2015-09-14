import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;

public class AttributeMultiFieldQueryParser
        extends MultiFieldQueryParser {

    protected AttributeQueryParserUtils attributeQueryParserUtils;

    public AttributeMultiFieldQueryParser(final String[] fields, final Analyzer analyzer) {
        super(fields, analyzer);

        attributeQueryParserUtils = new AttributeQueryParserUtils();
    }

    @Override
    protected Query getFieldQuery(String field, String queryText, boolean quoted)
            throws ParseException {
        Query query = attributeQueryParserUtils.getFieldQuery(field, fields, boosts, queryText, quoted);

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
        Query query = attributeQueryParserUtils.newRangeQuery(field, fields, min, max, startInclusive, endInclusive);
        
        return query == null ? super.newRangeQuery(field, min, max, startInclusive, endInclusive) : query;
    }

}
