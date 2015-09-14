

public class SortableDescriptionProducerFactory {

    private SortableDescriptionProducerFactory() {
        super();
    }

    private static class SortableDescriptionProducerFactoryHolder {
        static SortableDescriptionProducerFactory instance = new SortableDescriptionProducerFactory();
    }

    public static SortableDescriptionProducerFactory getInstance() {
        return SortableDescriptionProducerFactoryHolder.instance;
    }

    public SortableDescriptionProducer getSortableDescriptionProducer(String languageIsoName) {
        if(languageIsoName.equals("en")) {
            return new EnglishSortableDescriptionProducer();
        } else {
            return new UniversalSortableDescriptionProducer();
        }
    }
}
