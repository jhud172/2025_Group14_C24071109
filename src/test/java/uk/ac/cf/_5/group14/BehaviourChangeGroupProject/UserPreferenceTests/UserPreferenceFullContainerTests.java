package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserPreferenceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.Preference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecord;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecordRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalCondition;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalConditionRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserPreferenceFullContainerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PhysicalConditionRepository physicalConditionRepository;
    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void shouldGetTestCondition() throws Exception {

        // Given that a user's most recent health record contains the physical condition testCondition.

        User testUser = new User();
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setEmail("test@email.com");
        userRepository.save(testUser);

        PhysicalCondition testCondition = new PhysicalCondition();
        testCondition.setName("testCondition");
        physicalConditionRepository.save(testCondition);

        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setUser(testUser);
        healthRecord.setPhysicalConditions(List.of(testCondition));
        healthRecordRepository.save(healthRecord);

        // When the user goes to the select preference page, does not select anything and submits the form.

        MvcResult when = mvc
                .perform(post("/select-preferences")
                        .sessionAttr("user",testUser)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn();

        // Then testCondition should still be in their preference list.

        MvcResult then = mvc
                .perform(get("/preferences")
                        .sessionAttr("user",testUser)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = then.getResponse().getContentAsString();
        List<PhysicalCondition> physicalConditions = (List<PhysicalCondition>) then.getModelAndView().getModelMap().getAttribute("physicalConditions");

        assertTrue(content.contains("testCondition"));
        Assertions.assertNotNull(physicalConditions);
        assertTrue(physicalConditions.contains(testCondition));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void shouldGetNoPreferences() throws Exception {

        // Given that a user does not have a health record.

        User testUser = new User();
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setEmail("test@email.com");

        userRepository.save(testUser);

        // When a user does not select any preferences and submits the form.

        MvcResult when = mvc
                .perform(post("/select-preferences")
                        .sessionAttr("user",testUser)

                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn();

        // Then the preferences page states that they have not selected any selected preferences and that they do not have any saved conditions.

        MvcResult then = mvc
                .perform(get("/preferences")
                        .sessionAttr("user",testUser)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = then.getResponse().getContentAsString();
        List<Preference> preferences = (List<Preference>) then.getModelAndView().getModelMap().getAttribute("preferences");
        List<PhysicalCondition> physicalConditions = (List<PhysicalCondition>) then.getModelAndView().getModelMap().getAttribute("physicalConditions");

        assertTrue(content.contains("You have not selected any preferences."));
        assertTrue(content.contains("You do not have any saved conditions"));

        Assertions.assertNotNull(preferences);
        assertTrue(preferences.isEmpty());

        Assertions.assertNotNull(physicalConditions);
        assertTrue(physicalConditions.isEmpty());

    }
}
