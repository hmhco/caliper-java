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

package org.imsglobal.caliper.ri.events.development;

import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.imsglobal.caliper.events.HMHConstants.ACTIVITY_REF_ID;
import static org.imsglobal.caliper.events.HMHConstants.BASE_IRI;
import static org.imsglobal.caliper.events.HMHConstants.BASE_URN;
import static org.imsglobal.caliper.events.HMHConstants.COURSE_SECTION_ID;
import static org.imsglobal.caliper.events.HMHConstants.DISTRICT_REF_ID;
import static org.imsglobal.caliper.events.HMHConstants.SCHOOL_REF_ID;
import static org.imsglobal.caliper.events.HMHConstants.STUDENT_USER_REF_ID;
import static org.imsglobal.caliper.ri.events.RIConstants.APP_NAME;
import static org.imsglobal.caliper.ri.events.RIConstants.RIMI_DEVELOPMENT_DIRECTORY;
import static org.imsglobal.caliper.ri.events.RIConstants.APP_CODE;
import static org.imsglobal.caliper.ri.events.RIConstants.APP_CODE_KEY;
import static org.imsglobal.caliper.ri.events.RIConstants.DISCIPLINE_CODE;
import static org.imsglobal.caliper.ri.events.RIConstants.DISCIPLINE_CODE_KEY;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.imsglobal.caliper.TestUtils;
import org.imsglobal.caliper.actions.Action;
import org.imsglobal.caliper.context.JsonldContext;
import org.imsglobal.caliper.context.JsonldStringContext;
import org.imsglobal.caliper.entities.EntityType;
import org.imsglobal.caliper.entities.agent.CourseSection;
import org.imsglobal.caliper.entities.agent.Membership;
import org.imsglobal.caliper.entities.agent.Organization;
import org.imsglobal.caliper.entities.agent.Person;
import org.imsglobal.caliper.entities.agent.Role;
import org.imsglobal.caliper.entities.agent.SoftwareApplication;
import org.imsglobal.caliper.entities.agent.Status;
import org.imsglobal.caliper.entities.resource.Assessment;
import org.imsglobal.caliper.entities.session.Session;
import org.imsglobal.caliper.events.AssessmentEvent;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@Category(org.imsglobal.caliper.UnitTest.class)
public class AssessmentEventStartedTest {
    private JsonldContext context;
    private String id;
    private String member_id;
    private Person actor;
    private Assessment object;
    private SoftwareApplication edApp;
    private CourseSection group;
    private Membership membership;
    private Session session;
    private AssessmentEvent event;

    @Before
    public void setUp() throws Exception {
        context = JsonldStringContext.getDefault();

        id = BASE_URN + "27734504-068d-4596-861c-2315be33a2a2";
        member_id = BASE_URN + "randomGuidId";

        actor = Person.builder().id(BASE_URN.concat(STUDENT_USER_REF_ID)).build();

        object = Assessment.builder()
            .id(BASE_URN.concat(ACTIVITY_REF_ID))
            .name(APP_NAME)
            .maxAttempts(1)
            .maxSubmits(1)
            .maxScore(25.0)
            .version("1.0")
            .build();

        Map<String,Object> edAppExtensions = new HashMap<String,Object>();
        edAppExtensions.put(APP_CODE_KEY, APP_CODE);
        edAppExtensions.put(DISCIPLINE_CODE_KEY, DISCIPLINE_CODE);
        
        edApp = SoftwareApplication.builder().id(BASE_IRI.concat(APP_NAME))
                .type(EntityType.SOFTWARE_APPLICATION)
                    .extensions(edAppExtensions)
                    .build();

        membership = Membership.builder()
            .id(member_id)
            .organization(CourseSection.builder().id(BASE_URN.concat(COURSE_SECTION_ID)).type(EntityType.COURSE_SECTION).academicSession("BOY 2019")
                    .subOrganizationOf(Organization.builder().id(BASE_URN.concat(SCHOOL_REF_ID)).type(EntityType.ORGANIZATION)
                            .subOrganizationOf(Organization.builder().id(BASE_URN.concat(DISTRICT_REF_ID)).type(EntityType.ORGANIZATION).build())
                            .build())
                    .build())
            .status(Status.ACTIVE)
            .role(Role.LEARNER)
            .build();

        event = buildEvent(Action.STARTED);
    }

    @Test
    public void caliperEventSerializesToJSON() throws Exception {
        ObjectMapper mapper = TestUtils.createCaliperObjectMapper();
        String json = mapper.writeValueAsString(event);

        String fixture = jsonFixture(RIMI_DEVELOPMENT_DIRECTORY+"caliperEventAssessmentStarted.json");
        JSONAssert.assertEquals(fixture, json, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void assessmentEventRejectsSearchedAction() {
        buildEvent(Action.SEARCHED);
    }

    @After
    public void teardown() {
        event = null;
    }

    /**
     * Build Assessment event
     * @param action
     * @return event
     */
    private AssessmentEvent buildEvent(Action action) {
        return AssessmentEvent.builder()
            .context(context)
            .id(id)
            .actor(actor)
            .action(action)
            .object(object)
            .eventTime(new DateTime(2016, 11, 15, 10, 15, 0, 0, DateTimeZone.UTC))
            .edApp(edApp)
            .group(group)
            .membership(membership)
            .session(session)
            .build();
    }
}
