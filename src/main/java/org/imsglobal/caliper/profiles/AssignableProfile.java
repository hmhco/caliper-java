package org.imsglobal.caliper.profiles;

import com.google.common.collect.ImmutableMap;
import org.imsglobal.caliper.events.AssignableEvent;
import org.imsglobal.caliper.validators.AssignableEventValidator;
import org.imsglobal.caliper.validators.EventValidator;
import org.imsglobal.caliper.validators.EventValidatorContext;
import org.imsglobal.caliper.validators.ValidatorResult;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AssignableProfile {

    public enum Actions {
        ABANDONED("assignable.abandoned") {
            @Override
            ValidatorResult validate(AssignableEvent event) {
                EventValidatorContext validator;
                validator = new EventValidatorContext(new AssignableEventValidator());
                return validator.validate(event);
            }
        },
        ACTIVATED("assignable.activated") {
            @Override
            ValidatorResult validate(AssignableEvent event) {
                EventValidatorContext validator;
                validator = new EventValidatorContext(new AssignableEventValidator());
                return validator.validate(event);
            }
        },
        COMPLETED("assignable.completed") {
            @Override
            ValidatorResult validate(AssignableEvent event) {
                EventValidatorContext validator;
                validator = new EventValidatorContext(new AssignableEventValidator());
                return validator.validate(event);
            }
        },
        DEACTIVATED("assignable.deactivated") {
            @Override
            ValidatorResult validate(AssignableEvent event) {
                EventValidatorContext validator;
                validator = new EventValidatorContext(new AssignableEventValidator());
                return validator.validate(event);
            }
        },
        HID("assignable.hid") {
            @Override
            ValidatorResult validate(AssignableEvent event) {
                EventValidatorContext validator;
                validator = new EventValidatorContext(new AssignableEventValidator());
                return validator.validate(event);
            }
        },
        REVIEWED("assignable.reviewed") {
            @Override
            ValidatorResult validate(AssignableEvent event) {
                EventValidatorContext validator;
                validator = new EventValidatorContext(new AssignableEventValidator());
                return validator.validate(event);
            }
        },
        SHOWED("assignable.showed") {
            @Override
            ValidatorResult validate(AssignableEvent event) {
                EventValidatorContext validator;
                validator = new EventValidatorContext(new AssignableEventValidator());
                return validator.validate(event);
            }
        },
        STARTED("assignable.started") {
            @Override
            ValidatorResult validate(AssignableEvent event) {
                EventValidatorContext validator;
                validator = new EventValidatorContext(new AssignableEventValidator());
                return validator.validate(event);
            }
        },
        UNRECOGNIZED("action.unrecognized") {
            @Override
            ValidatorResult validate(AssignableEvent event) {
                ValidatorResult result = new ValidatorResult();
                result.errorMessage().appendText("Caliper Assessment profile conformance: "
                    + EventValidator.Conformance.ACTION_UNRECOGNIZED.violation());
                result.errorMessage().endSentence();
                return result;
            }
        };

        private final String key;
        private static Map<String, Actions> lookup;

        /**
         * Create reverse lookup hash map
         */
        static {
            Map<String, Actions> map = new HashMap<String, Actions>();
            for (Actions constants : Actions.values()) {
                map.put(constants.key(), constants);
            }
            lookup = ImmutableMap.copyOf(map);
        }

        /**
         * Private constructor
         * @param key
         */
        private Actions(final String key) {
            this.key = key;
        }

        /**
         * Resource bundle key
         * @return key
         */
        public String key() {
            return key;
        }

        /**
         * @param key
         * @return true if lookup returns a key match; false otherwise.
         */
        public static boolean hasKey(String key) {
            return lookup.containsKey(key);
        }

        /**
         * Lookup key by comparing localized action string against matching bundle value.
         * @param action
         * @return
         */
        public static String lookupKey(String action) {
            ResourceBundle bundle = ResourceBundle.getBundle("actions");
            for (Map.Entry<String, Actions> entry: lookup.entrySet()) {
                if (action.equals(bundle.getString(entry.getKey()))) {
                    return entry.getKey();
                }
            }
            return Actions.UNRECOGNIZED.key();
        }

        /**
         * Validate method implemented by each enum constant.
         * @param event
         */
        abstract ValidatorResult validate(AssignableEvent event);

        /**
         * Match action to enum constant and then validate event.
         * @param event
         * @return error message if validation errors are encountered.
         */
        protected static ValidatorResult validateEvent(AssignableEvent event) {
            return Actions.matchConstant(event.getAction()).validate(event);
        }

        /**
         * Match the event action string against the bundle value and return
         * the corresponding constant.
         * @param action
         * @return constant
         */
        private static Actions matchConstant(String action) {
            ResourceBundle bundle = ResourceBundle.getBundle("actions");
            for (Map.Entry<String, Actions> entry: lookup.entrySet()) {
                if (action.equals(bundle.getString(entry.getKey()))) {
                    return entry.getValue();
                }
            }
            return Actions.UNRECOGNIZED;
        }
    }

    /**
     * Constructor
     */
    public AssignableProfile() {

    }

    /**
     * Validate AssessmentItemEvent.
     * @param event
     * @return ValidatorResult
     */
    public static ValidatorResult validateEvent(@Nonnull AssignableEvent event) {
        return Actions.validateEvent(event);
    }
}