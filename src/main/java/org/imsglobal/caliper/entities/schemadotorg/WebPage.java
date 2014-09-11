package org.imsglobal.caliper.entities.schemadotorg;

import org.imsglobal.caliper.entities.CaliperDigitalResource;

public class WebPage extends CaliperDigitalResource implements CreativeWork {

    private final String type;

    /**
     * @param builder apply builder object properties to the WebPage object.
     */
    protected WebPage(Builder<?> builder) {
        super(builder);
        this.type = builder.type;
    }

    /**
     * @return the type
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Builder class provides a fluid interface for setting object properties.
     * @param <T> builder
     */
    public static abstract class Builder<T extends Builder<T>> extends CaliperDigitalResource.Builder<T>  {
        private static final String WEBPAGE_TYPE = "http://purl.imsglobal.org/caliper/v1/WebPage";
        private String type;

        /**
         * Initialize type with default value.  Required if builder().type() is not set by user.
         */
        public Builder() {
            type(WEBPAGE_TYPE);
        }

        /**
         * @param type
         * @return the IMS Global type reference URI.
         */
        @Override
        public T type(String type) {
            if (type.equals(WEBPAGE_TYPE)) {
                this.type = type;
            } else {
                this.type = WEBPAGE_TYPE;
            }
            return self();
        }

        /**
         * Client invokes build method in order to create an immutable object.
         * @return a new instance of WebPage.
         */
        public WebPage build() {
            return new WebPage(this);
        }
    }

    /**
     *
     */
    private static class Builder2 extends Builder<Builder2> {
        @Override
        protected Builder2 self() {
            return this;
        }
    }

    /**
     * Static factory method.
     * @return a new instance of the builder.
     */
    public static Builder<?> builder() {
        return new Builder2();
    }
}
