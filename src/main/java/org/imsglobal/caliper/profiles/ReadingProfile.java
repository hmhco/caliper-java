package org.imsglobal.caliper.profiles;

import java.util.ArrayList;
import java.util.List;

public class ReadingProfile extends BaseProfile {

    private List<Object> frames = new ArrayList<Object>();
    private List<Object> navigatedFroms = new ArrayList<Object>();

    /**
     * Constructor
     * @param builder apply builder object properties to the profile object.
     */
    protected ReadingProfile(Builder<?> builder) {
        super(builder);
        this.frames = builder.frames;
        this.navigatedFroms = builder.navigatedFroms;
    }

    /**
     * A frame registers the location or range within a piece of content (a page, a section, or epub cfi).
     * @return List of frames
     */
    public List<Object> getFrames() {
        return frames;
    }

    /**
     * @return return a list of starting locations.
     */
    public List<Object> getNavigatedFroms() {
        return navigatedFroms;
    }

    /**
     * Initialize default parameter values in the builder (not in the outer profile class).
     * @param <T> builder
     */
    public static abstract class Builder<T extends Builder<T>> extends BaseProfile.Builder<T>  {

        private List<Object> frames = new ArrayList<Object>();
        private List<Object> navigatedFroms = new ArrayList<Object>();

        /**
         * @param frames
         * @return builder.
         */
        public T frames(List<Object> frames) {
            this.frames = frames;
            return self();
        }

        /**
         * @param frame
         * @return builder
         */
        public T frame(Object frame) {
            this.frames.add(frame);
            return self();
        }

        /**
         * @param navigatedFroms
         * @return builder.
         */
        public T navigatedFroms(List<Object> navigatedFroms) {
            this.navigatedFroms = navigatedFroms;
            return self();
        }

        /**
         * @param navigatedFrom
         * @return builder
         */
        public T navigatedFrom(Object navigatedFrom) {
            this.navigatedFroms.add(navigatedFrom);
            return self();
        }

        /**
         * Client invokes the build method in order to create an immutable profile object.
         * @return a new instance of MediaProfile.
         */
        public ReadingProfile build() {
            return new ReadingProfile(this);
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