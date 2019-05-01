package org.imsglobal.caliper.insighted.events.development;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.imsglobal.caliper.CaliperSendable;
import org.imsglobal.caliper.Envelope;
import org.imsglobal.caliper.Sensor;
import org.imsglobal.caliper.TestUtils;
import org.imsglobal.caliper.actions.Action;
import org.imsglobal.caliper.config.Config;
import org.imsglobal.caliper.context.JsonldContext;
import org.imsglobal.caliper.context.JsonldStringContext;
import org.imsglobal.caliper.entities.EntityType;
import org.imsglobal.caliper.entities.agent.Agent;
import org.imsglobal.caliper.entities.agent.CaliperAgent;
import org.imsglobal.caliper.entities.agent.CourseSection;
import org.imsglobal.caliper.entities.agent.Membership;
import org.imsglobal.caliper.entities.agent.Organization;
import org.imsglobal.caliper.entities.agent.Person;
import org.imsglobal.caliper.entities.agent.Role;
import org.imsglobal.caliper.entities.agent.SoftwareApplication;
import org.imsglobal.caliper.entities.outcome.Score;
import org.imsglobal.caliper.entities.resource.Assessment;
import org.imsglobal.caliper.entities.resource.Attempt;
import org.imsglobal.caliper.events.GradeEvent;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;

@Category(org.imsglobal.caliper.UnitTest.class)
public class CaliperGradeEventScoreTest {

  private static final String BASE_URN = "urn:uuid:";
  private static final String AGGREGATOR_PROGRAM_ASSESSMENT_CONTEXT = "https://www.hmhco.com/AggPA";
  private static final String ID = BASE_URN.concat("dc790a2d-124e-4b20-9f89-2bca87ea22d4");
  private static final String ATTEMPT_ID = BASE_URN.concat("830b77b2-f4b3-4ac3-87b0-a4fef1d7c33e");
  private static final String SCORE_ID = BASE_URN.concat("7617bd21-09bb-41fa-b2f5-d707294bce2c");
  private static final String MEMBERSHIP_ID = BASE_URN.concat("6b8fb8bb-a4b1-4367-be53-9cb78be9dab5");
  private static final String LEARNER_ID = BASE_URN.concat("d76d4cbe-0f12-4323-af07-e68566dcbcd4");
  private static final String INSTRUCTOR_ID = BASE_URN.concat("7eba360f-5896-48d8-b8ce-23ad40892abd");
  private static final String ASSIGNMENT_ID = BASE_URN.concat("376835ba-947a-47f3-a990-003c7eb11cfc");
  private static final String DISTRICT_ID = BASE_URN.concat("c2fb58c4-2d99-4e13-a570-24fc9dc160a5");
  private static final String SCHOOL_ID = BASE_URN.concat("baa92ba2-9d9f-4a76-8e5e-c89a5a2a09dc");
  private static final String CLASS_ID = BASE_URN.concat("dd1a3d98-5fba-466c-bff5-4eeab14672e0");
  private static final String CREATOR_ID = "https://www.renaissance.com";
  private static final String DISCIPLINE_CODE = "ED18_RLA";
  private static DateTime EVENT_TIME = new DateTime(2019, 3, 22, 17, 46, 40, 999, DateTimeZone.UTC);
  private static DateTime SEND_TIME = new DateTime(2019, 3, 22, 17, 46, 40, 111, DateTimeZone.UTC);

  private JsonldContext context;
  private SoftwareApplication edApp;
  private Person learner;
  private CourseSection courseSection;
  private Attempt object;
  private Score score;
  private Membership membership;
  private GradeEvent event;
  private Envelope envelope;

  @Before
  public void setUp() throws Exception {
    context = JsonldStringContext.getDefault();

    edApp = SoftwareApplication.builder().id(AGGREGATOR_PROGRAM_ASSESSMENT_CONTEXT).coercedToId(true).build();
    learner = Person.builder().id(LEARNER_ID).build();
    CaliperAgent creator = Agent.builder().id(CREATOR_ID).coercedToId(true).build();

    Map<String, Object> assignableExtensions = new HashMap<>();
    assignableExtensions.put("disciplineCode", DISCIPLINE_CODE);

    Assessment assignable = Assessment.builder()
      .id(ASSIGNMENT_ID)
      .creators(Collections.singletonList(creator))
      .extensions(assignableExtensions)
      .build();

    object = Attempt.builder()
      .id(ATTEMPT_ID)
      .assignable(assignable)
      .assignee(Person.builder().id(LEARNER_ID).coercedToId(true).build())
      .build();

    score = Score.builder()
      .id(SCORE_ID)
      .attempt(Attempt.builder().id(ATTEMPT_ID).coercedToId(true).build())
      .maxScore(12)
      .scoreGiven(6)
      .scoredBy(SoftwareApplication.builder().id(AGGREGATOR_PROGRAM_ASSESSMENT_CONTEXT).coercedToId(true).build())
      .build();

    Map<String, Object> courseSectionExtensions = new HashMap<>();
    courseSectionExtensions.put("instructor", INSTRUCTOR_ID);

    Organization district = Organization.builder().id(DISTRICT_ID).build();
    Organization school = Organization.builder().id(SCHOOL_ID).subOrganizationOf(district).build();

    courseSection = CourseSection.builder()
      .id(CLASS_ID)
      .extensions(courseSectionExtensions)
      .subOrganizationOf(school)
      .build();

    membership = Membership.builder()
      .id(MEMBERSHIP_ID)
      .organization(Organization.builder().id(SCHOOL_ID).type(EntityType.ORGANIZATION)
          .subOrganizationOf(Organization.builder().id(DISTRICT_ID).type(EntityType.ORGANIZATION)
        .build()).build())
      .role(Role.LEARNER)
      .build();

    event = buildEvent(Action.GRADED);

    List<CaliperSendable> data = new ArrayList<>();
    data.add(event);

    Sensor sensor = Sensor.create(AGGREGATOR_PROGRAM_ASSESSMENT_CONTEXT);
    envelope = sensor.create(sensor.getId(), SEND_TIME, Config.DATA_VERSION, data);
  }

  @Test
  public void caliperEventSerializesToJSON() throws Exception {
    String fixture = jsonFixture("fixtures/hmh/hmh-insighted/development/caliperGradeEventScore.json");

    handleSendTimeProperty(fixture);

    ObjectMapper mapper = TestUtils.createCaliperObjectMapper();
    String json = mapper.writeValueAsString(envelope);

    JSONAssert.assertEquals(fixture, json, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test(expected=IllegalArgumentException.class)
  public void gradeEventRejectsHidAction() {
      buildEvent(Action.HID);
  }

  @After
  public void teardown() {
      event = null;
  }

  private GradeEvent buildEvent(Action action) {
    return GradeEvent.builder()
      .context(context)
      .id(ID)
      .actor(learner)
      .action(action)
      .object(object)
      .generated(score)
      .edApp(edApp)
      .group(courseSection)
      .membership(membership)
      .eventTime(EVENT_TIME)
      .build();
  }

  // Workaround: sendTime is not being assigned when passed to Sensor's 'create' (Envelope) function
  private void handleSendTimeProperty(String fixture) throws IOException, NoSuchFieldException, IllegalAccessException {
    JsonNode node = new ObjectMapper().readTree(fixture);
    String platformSendTime = node.get("sendTime").asText();
    Field field = (Envelope.class).getDeclaredField("sendTime");
    field.setAccessible(true);
    field.set(envelope, DateTime.parse(platformSendTime));
  }
}