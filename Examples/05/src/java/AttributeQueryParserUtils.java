import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.NumericUtils;

public class AttributeQueryParserUtils {

    public AttributeQueryParserUtils() {
    }

    protected Integer intValueOf(final String fieldValue) {
        Integer result = null;
        
        if(fieldValue.equalsIgnoreCase("MAX_VALUE")) {
            result = Integer.MAX_VALUE;
        } else if(fieldValue.equalsIgnoreCase("MIN_VALUE")) {
            result = Integer.MIN_VALUE;
        } else {
                result = Integer.valueOf(fieldValue);
        }
        
        return result;
    }
    
    protected Integer intValueOf(final String fieldValue, final Integer minimumValue, final Integer maximumValue) {
        Integer value;
        
        if(fieldValue.equalsIgnoreCase("MAX_VALUE")) {
            value = maximumValue == null ? Integer.MAX_VALUE : maximumValue;
        } else if(fieldValue.equalsIgnoreCase("MIN_VALUE")) {
            value = minimumValue == null ? Integer.MIN_VALUE : minimumValue;
        } else {
            value = Integer.valueOf(fieldValue);
        }
        
        return value;
    }
    
    protected BooleanQuery newBooleanQuery(boolean disableCoord) {
        return new BooleanQuery.Builder().setDisableCoord(disableCoord).build();
    }
    
    protected Query getBooleanQuery(List<BooleanClause> clauses, boolean disableCoord) {
        BooleanQuery.Builder builder;
        
        if(clauses.isEmpty()) {
            builder = null; // all clause words were filtered away by the analyzer.
        } else {
            builder = new BooleanQuery.Builder();

            builder.setDisableCoord(disableCoord);
            
            clauses.stream().forEach((clause) -> {
                builder.add(clause);
            });
        }
        
        return builder == null ? null : builder.build();
    }
  
    protected Query getFieldQuery(String rawField, String[] rawFields, Map<String,Float> boosts, String queryText, boolean quoted)
            throws ParseException {
        Query query;
        
        // rawField is null when multiple fields are being searched by default.
        if(rawField == null) {
            List<BooleanClause> clauses = new ArrayList<>(rawFields.length);
            
            for(int i = 0; i < rawFields.length; i++) {
                Query q = getFieldQuery(rawFields[i], rawFields, boosts, queryText, quoted);
                
                if(q != null) {
                    //I f the user passes a map of boosts
                    if(boosts != null) {
                        // Get the boost from the map and apply it.
                        Float boost = boosts.get(rawFields[i]);
                        
                        if (boost != null) {
                            q.setBoost(boost);
                        }
                    }
                    
                    clauses.add(new BooleanClause(q, BooleanClause.Occur.SHOULD));
                }
            }
            
            if (clauses.isEmpty()) {
                // happens for stopwords
                return null;
            } else {
                query = getBooleanQuery(clauses, true);
            }
        } else {
            query = newTermQuery(new Term(rawField, queryText));
        }
        
        return query;
    }
    
    public Query newTermQuery(final Term term) {
        Query query = null;
        String field = term.field();
        
        if(field.equals("released")) {
            Integer val = intValueOf(term.text());
            BytesRefBuilder bytesRefBuilder = new BytesRefBuilder();

            NumericUtils.intToPrefixCoded(val, 0, bytesRefBuilder);
            query = new TermQuery(new Term(field, bytesRefBuilder.get()));
        }
        
        return query;
    }

    public Query newRangeQuery(final String rawField, String[] rawFields, final String min, final String max, final boolean startInclusive, final boolean endInclusive) {
        Query query = null;
        
        if(rawField == null) {
            List<BooleanClause> clauses = new ArrayList<>(rawFields.length);
            
            for(int i = 0; i < rawFields.length; i++) {
                clauses.add(new BooleanClause(newRangeQuery(rawFields[i], null, min, max, startInclusive, endInclusive), BooleanClause.Occur.SHOULD));
            }
            
            query = getBooleanQuery(clauses, true);
        } else {
            if(rawField.equals("released")) {
                Integer valMin = intValueOf(min);
                Integer valMax = intValueOf(max);

                query = NumericRangeQuery.newIntRange(rawField, valMin, valMax, startInclusive, endInclusive);
            }
        }
        
        return query;
    }

}
