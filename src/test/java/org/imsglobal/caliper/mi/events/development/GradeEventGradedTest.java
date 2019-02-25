/**
 * This file is part of IMS Caliper Analytics™ and is licensed to
 * IMS Global Learning Consortium, Inc. (http://www.imsglobal.org)
 * under one or more contributor license agreements.  See the NOTICE
 * file distributed with this work for additional information.
 *
 * IMS Caliper is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * IMS Caliper is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.imsglobal.caliper.mi.events.development;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.imsglobal.caliper.TestUtils;
import org.imsglobal.caliper.actions.Action;
import org.imsglobal.caliper.context.JsonldContext;
import org.imsglobal.caliper.context.JsonldStringContext;
import org.imsglobal.caliper.entities.EntityType;
import org.imsglobal.caliper.entities.agent.Membership;
import org.imsglobal.caliper.entities.agent.Organization;
import org.imsglobal.caliper.entities.agent.Person;
import org.imsglobal.caliper.entities.agent.Role;
import org.imsglobal.caliper.entities.agent.SoftwareApplication;
import org.imsglobal.caliper.entities.agent.Status;
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

import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.imsglobal.caliper.events.HMHConstants.ACTIVITY_REF_ID;
import static org.imsglobal.caliper.events.HMHConstants.BASE_IRI;
import static org.imsglobal.caliper.events.HMHConstants.BASE_URN;
import static org.imsglobal.caliper.events.HMHConstants.DISTRICT_REF_ID;
import static org.imsglobal.caliper.events.HMHConstants.LAST_ATTEMPT_ID;
import static org.imsglobal.caliper.events.HMHConstants.OBJECT_ID;
import static org.imsglobal.caliper.events.HMHConstants.SCHOOL_REF_ID;
import static org.imsglobal.caliper.events.HMHConstants.STUDENT_USER_REF_ID;
import static org.imsglobal.caliper.mi.events.MIConstants.APP_NAME;
import static org.imsglobal.caliper.mi.events.MIConstants.MI_DEVELOPMENT_DIRECTORY;

@Category(org.imsglobal.caliper.UnitTest.class)
public class GradeEventGradedTest {
    private JsonldContext context;
    private String id;
    private String member_id;
    private String generated_id;
    private SoftwareApplication edApp;
    private Person actor;
    private Attempt object;
    private Assessment assignable;
    private Score generated;
    private Membership membership;
    private GradeEvent event;

    @Before
    public void setUp() throws Exception {
        context = JsonldStringContext.getDefault();

        id = "urn:uuid:ae54223c-d645-4947-9d94-f0171db75c7c";
        member_id = "urn:uuid:randomGuidId";
        generated_id = "urn:uuid:a9f627b3-8678-4652-8239-b174c329f599";

        //actor = SoftwareApplication.builder().id(BASE_IRI.concat(APP_NAME)).version("v2").build();
        actor = Person.builder().id(BASE_URN.concat(STUDENT_USER_REF_ID)).build();
        assignable = Assessment.builder().id(BASE_URN.concat(ACTIVITY_REF_ID)).build();

        object = Attempt.builder()
            .id(BASE_URN.concat(LAST_ATTEMPT_ID))
            .assignable(assignable)
            .assignee(Person.builder().id(actor.getId()).coercedToId(true).build())
            .count(35)
            .dateCreated(new DateTime(2016, 11, 15, 10, 5, 0, 0, DateTimeZone.UTC))
            .startedAtTime(new DateTime(2016, 11, 15, 10, 5, 0, 0, DateTimeZone.UTC))
            .endedAtTime(new DateTime(2016, 11, 15, 10, 55, 12, 0, DateTimeZone.UTC))
            .duration("PT30M")
            .build();

        generated = Score.builder()
            .id(generated_id)
            .attempt(Attempt.builder().id(BASE_URN.concat(LAST_ATTEMPT_ID)).coercedToId(true).build())
            .maxScore(1500)
            .scoreGiven(800)
            .scoredBy(SoftwareApplication.builder().id(BASE_IRI.concat(APP_NAME)).coercedToId(true).build())
            .comment("The quantile score from Test # "+OBJECT_ID+" taken on date/time for student " + STUDENT_USER_REF_ID)
            .dateCreated(new DateTime(2016, 11, 15, 10, 56, 0, 0, DateTimeZone.UTC))
            .build();

        edApp = SoftwareApplication.builder().id(BASE_IRI.concat(APP_NAME)).coercedToId(true).build();

        membership = Membership.builder()
                .id(member_id)
                .organization(Organization.builder().id(BASE_URN.concat(SCHOOL_REF_ID)).type(EntityType.ORGANIZATION)
                    .subOrganizationOf(Organization.builder().id(BASE_URN.concat(DISTRICT_REF_ID)).type(EntityType.ORGANIZATION)
                            .build())
                    .build())
                .status(Status.ACTIVE)
                .role(Role.LEARNER)
                .build();

        // Build Outcome Event
        event = buildEvent(Action.GRADED);
    }

    @Test
    public void caliperEventSerializesToJSON() throws Exception {
        ObjectMapper mapper = TestUtils.createCaliperObjectMapper();
        String json = mapper.writeValueAsString(event);

        String fixture = jsonFixture(MI_DEVELOPMENT_DIRECTORY+"caliperEventGradeGraded.json");
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

    /**
     * Build Grade event.
     * @param action
     * @return event
     */
    private GradeEvent buildEvent(Action action) {
        return GradeEvent.builder()
            .context(context)
            .id(id)
            .actor(actor)
            .action(action)
            .object(object)
            .generated(generated)
            .edApp(edApp)
            .membership(membership)
            .eventTime(new DateTime(2016, 11, 15, 10, 57, 6, 0, DateTimeZone.UTC))
            .build();
    }
}
