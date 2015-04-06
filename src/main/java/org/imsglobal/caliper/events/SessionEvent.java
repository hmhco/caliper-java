package org.imsglobal.caliper.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.imsglobal.caliper.entities.DigitalResource;
import org.imsglobal.caliper.entities.agent.Person;
import org.imsglobal.caliper.entities.schemadotorg.SoftwareApplication;
import org.imsglobal.caliper.entities.session.Session;
import org.imsglobal.caliper.profiles.Profile.Action;
import org.imsglobal.caliper.validators.EventValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@SupportedActions({
        Action.LOGGED_IN,
        Action.LOGGED_OUT,
        Action.TIMED_OUT
})
public class SessionEvent extends Event {

    @JsonProperty("@context")
    private final String context;

    @JsonProperty("@type")
    private final String type;

    @JsonProperty("action")
    private final Action action;

    @JsonIgnore
    private static final Logger log = LoggerFactory.getLogger(SessionEvent.class);

    /**
     * Utilize builder to construct SessionEvent.  Validate Session object copy rather than the
     * Session builder.  This approach protects the class against parameter changes from another
     * thread during the "window of vulnerability" between the time the parameters are checked
     * until when they are copied.
     *
     * @param builder
     */
    protected SessionEvent(Builder<?> builder) {
        super(builder);

        EventValidator.checkContextUri(builder.context, Context.SESSION);
        EventValidator.checkTypeUri(builder.type, Type.SESSION);
        EventValidator.checkAction(builder.action, SessionEvent.class);

        if (builder.action == Action.TIMED_OUT) {
            EventValidator.checkActorType(getActor(), SoftwareApplication.class);
        } else {
            EventValidator.checkActorType(getActor(), Person.class);
        }

        EventValidator.checkObjectType(getObject(), SoftwareApplication.class);

        if (builder.action == Action.LOGGED_IN) {
            EventValidator.checkTargetType(getTarget(), DigitalResource.class);
            EventValidator.checkGeneratedType(getGenerated(), Session.class);
        } else {
            EventValidator.checkTargetType(getTarget(), Session.class);
        }

        if (builder.action == Action.LOGGED_OUT || builder.action == Action.TIMED_OUT) {
            EventValidator.checkEndTime(getStartedAtTime(), getEndedAtTime());
        }

        this.context = builder.context;
        this.type = builder.type;
        this.action = builder.action;
    }

    /**
     * Required.
     * @return the context
     */
    @Override
    @Nonnull
    public String getContext() {
        return context;
    }

    /**
     * Required.
     * @return the type
     */
    @Override
    @Nonnull
    public String getType() {
        return type;
    }

    /**
     * Required.
     * @return the action
     */
    @Override
    @Nonnull
    public Action getAction() {
        return action;
    }

    /**
     * Initialize default parameter values in the builder.
     * @param <T> builder
     */
    public static abstract class Builder<T extends Builder<T>> extends Event.Builder<T>  {
        private String context;
        private String type;
        private Action action;

        /*
         * Constructor
         */
        public Builder() {
            context(Context.SESSION.uri());
            type(Type.SESSION.uri());
        }

        /**
         * @param context
         * @return builder.
         */
        private T context(String context) {
            this.context = context;
            return self();
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
         * @param action
         * @return builder.
         */
        @Override
        public T action(Action action) {
            this.action = action;
            return self();
        }

        /**
         * Client invokes build method in order to create an immutable profile object.
         * @return a new SessionEvent instance.
         */
        public SessionEvent build() {
            return new SessionEvent(this);
        }
    }

    /**
     * Self-reference that permits sub-classing of builder.
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