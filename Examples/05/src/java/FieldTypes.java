import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

public class FieldTypes {

    private FieldTypes() {
        super();
    }
    
    /**
     * Indexed, stored, not tokenized. This is typically used when storing an EntityRef in an indexed document.
     */
    public static final FieldType STORED_NOT_TOKENIZED = new FieldType();
    
    /**
     * Indexed, not stored, tokenized. This is typically used for descriptions of things.
     */
    public static final FieldType NOT_STORED_TOKENIZED = new FieldType();
    
    /**
     * Indexed, stored, tokenized.
     */
    public static final FieldType STORED_TOKENIZED = new FieldType();

    static {
        STORED_NOT_TOKENIZED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        STORED_NOT_TOKENIZED.setStored(true);
        STORED_NOT_TOKENIZED.setTokenized(false);
        STORED_NOT_TOKENIZED.freeze();

        NOT_STORED_TOKENIZED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        NOT_STORED_TOKENIZED.setStored(false);
        NOT_STORED_TOKENIZED.setTokenized(true);
        NOT_STORED_TOKENIZED.freeze();

        STORED_TOKENIZED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        STORED_TOKENIZED.setStored(true);
        STORED_TOKENIZED.setTokenized(true);
        STORED_TOKENIZED.freeze();
    }

}
