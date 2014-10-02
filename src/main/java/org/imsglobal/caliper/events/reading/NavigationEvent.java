package org.imsglobal.caliper.events.reading;

import org.imsglobal.caliper.actions.ReadingActions;
import org.imsglobal.caliper.entities.CaliperDigitalResource;
import org.imsglobal.caliper.events.CaliperEvent;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ResourceBundle;

public class NavigationEvent extends CaliperEvent {

    public enum Identifier {
        CONTEXT("http://purl.imsglobal.org/ctx/caliper/v1/NavigationEvent"),
        TYPE("http://purl.imsglobal.org/caliper/v1/NavigationEvent");

        private final String uri;

        /**
         * Private constructor
         * @param uri
         */
        private Identifier(final String uri) {
            this.uri = uri;
        }

        /**
         * @return URI string
         */
        public String uri() {
            return uri;
        }
    }

    /**
     * Navigation action
     */
    private final String action;

    /**
     * Describes the resource from which the navigation starts
     */
    @JsonProperty("navigatedFrom")
    private CaliperDigitalResource fromResource;

    /**
     * @param builder apply builder object properties to the NavigationEvent object.
     */
    protected NavigationEvent(Builder<?> builder) {
        super(builder);
        this.action = builder.action;
        this.fromResource = builder.fromResource;
    }

    /**
     * @return the action
     */
    @Override
    public String getAction() {
        return action;
    }

    /**
     * @return the fromResource
     */
    public CaliperDigitalResource getFromResource() {
        return fromResource;
    }

    /**
     * Builder class provides a fluid interface for setting object properties.
     * @param <T> builder
     */
    public static abstract class Builder<T extends Builder<T>> extends CaliperEvent.Builder<T>  {
        private String action;
        private CaliperDigitalResource fromResource;

        /**
         * Initialize type with default valueS.  Required if .builder() properties are not set by user.
         */
        public Builder() {
            context(NavigationEvent.Identifier.CONTEXT.uri());
            type(NavigationEvent.Identifier.TYPE.uri());
            action(ReadingActions.NAVIGATEDTO.key());
        }

        /**
         * @param key
         * @return builder
         */
        @Override
        public T action(String key) {
            if (ReadingActions.hasKey(key)) {
                this.action = ResourceBundle.getBundle("actions").getString(key);
                return self();
            } else {
                throw new IllegalArgumentException("Unrecognized constant: " + key);
            }
        }

        /**
         * @param fromResource
         * @return builder
         */
        public T fromResource(CaliperDigitalResource fromResource) {
            this.fromResource = fromResource;
            return self();
        }

        /**
         * Client invokes build method in order to create an immutable object.
         * @return a new instance of NavigationEvent.
         */
        public NavigationEvent build() {
            return new NavigationEvent(this);
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