package org.imsglobal.caliper.events;

import com.google.common.collect.Lists;
import org.imsglobal.caliper.actions.Action;
import org.imsglobal.caliper.entities.LearningContext;
import org.imsglobal.caliper.TestAgentEntities;
import org.imsglobal.caliper.TestDates;
import org.imsglobal.caliper.TestEpubEntities;
import org.imsglobal.caliper.TestLisEntities;
import org.imsglobal.caliper.entities.agent.Person;
import org.imsglobal.caliper.entities.annotation.TagAnnotation;
import org.imsglobal.caliper.entities.reading.EpubSubChapter;
import org.imsglobal.caliper.entities.reading.Frame;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.junit.Assert.assertEquals;

@Category(org.imsglobal.caliper.UnitTest.class)
public class TagAnnotationEventTest extends EventTest {

    private LearningContext learningContext;
    private EpubSubChapter ePub;
    private Frame object;
    private TagAnnotation generated;
    private AnnotationEvent event;
    private DateTime dateCreated = TestDates.getDefaultDateCreated();
    private DateTime dateModified = TestDates.getDefaultDateModified();
    private DateTime dateStarted = TestDates.getDefaultStartedAtTime();
    // private static final Logger log = LoggerFactory.getLogger(TagAnnotationEventTest.class);

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        // Build the Learning Context
        learningContext = LearningContext.builder()
            .edApp(TestAgentEntities.buildReadiumViewerApp())
            .group(TestLisEntities.buildGroup())
            .agent(TestAgentEntities.buildStudent554433())
            .build();

        //Build target reading
        ePub = TestEpubEntities.buildEpubSubChap434();

        // Build Frame
        object = Frame.builder()
            .id(ePub.getId())
            .name(ePub.getName())
            .isPartOf(ePub.getIsPartOf())
            .dateCreated(dateCreated)
            .dateModified(dateModified)
            .version(ePub.getVersion())
            .index(4)
            .build();

        // Add Tags
        List<String> tags = Lists.newArrayList();
        tags.add("to-read");
        tags.add("1765");
        tags.add("shared-with-project-team");

        // Build Tag Annotation
        generated = TagAnnotation.builder()
            .id("https://someEduApp.edu/tags/7654")
            .annotated(object)
            .tags(tags)
            .dateCreated(dateCreated)
            .dateModified(dateModified)
            .build();

        // Build event
        event = AnnotationEvent.builder()
            .edApp(learningContext.getEdApp())
            .group(learningContext.getGroup())
            .actor((Person) learningContext.getAgent())
            .action(Action.TAGGED)
            .object(object)
            .generated(generated)
            .startedAtTime(dateStarted)
            .build();

        // Build event
        event = buildEvent(Action.TAGGED);
    }

    @Test
    public void caliperEventSerializesToJSON() throws Exception {
        assertEquals("Test if Tag Annotation event is serialized to JSON with expected values",
            jsonFixture("fixtures/caliperTagAnnotationEvent.json"), serialize(event));
    }

    @Test(expected=IllegalArgumentException.class)
    public void annotationEventRejectsPausedAction() {
        buildEvent(Action.PAUSED);
    }

    /**
     * Build Annotation event.
     * @param action
     * @return event
     */
    private AnnotationEvent buildEvent(Action action) {
        return AnnotationEvent.builder()
            .edApp(learningContext.getEdApp())
            .group(learningContext.getGroup())
            .actor((Person) learningContext.getAgent())
            .action(action)
            .object(object)
            .generated(generated)
            .startedAtTime(dateStarted)
            .build();
    }
}