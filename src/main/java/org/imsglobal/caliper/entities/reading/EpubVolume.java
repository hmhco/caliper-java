package org.imsglobal.caliper.entities.reading;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.imsglobal.caliper.entities.DigitalResource;

import javax.annotation.Nonnull;

/**
 * Representation of an EPUB 3 Volume
 * 
 * A component of a collection
 * http://www.idpf.org/epub/vocab/structure/#volume
 */
public class EpubVolume extends DigitalResource {

    @JsonProperty("@type")
    private final String type;

    /**
     * @param builder apply builder object properties to the EpubVolume object.
     */
    protected EpubVolume(Builder<?> builder) {
        super(builder);
        this.type = builder.type;
    }

    /**
     * @return the type
     */
    @Override
    @Nonnull
    public String getType() {
        return type;
    }

    /**
     * Builder class provides a fluid interface for setting object properties.
     * @param <T> builder
     */
    public static abstract class Builder<T extends Builder<T>> extends DigitalResource.Builder<T>  {
        private String type;

        /**
         * Initialize type with default value.
         */
        public Builder() {
            type(DigitalResource.Type.EPUB_VOLUME.uri());
        }

        /**
         * @param type
         * @return builder.
         */
        private T type(String type) {
            this.type = type;
            return self();
        }

        /**
         * Client invokes build method in order to create an immutable object.
         * @return a new instance of EpubVolume.
         */
        public EpubVolume build() {
            return new EpubVolume(this);
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