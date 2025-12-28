package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserPreferenceTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.PreferenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference.UserPreferenceController;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference.UserPreferenceForm;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference.UserPreferenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalConditionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserPreferenceController.class)
public class UserPreferenceControllerOnlyTest {

    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserPreferenceService userPreferenceService;
    @MockitoBean
    private PreferenceService preferenceService;
    @MockitoBean
    private PhysicalConditionService physicalConditionService;
    @MockitoBean
    private AuthHelper authHelper;


    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void shouldRedirectAfterEmptyFormSubmission() throws Exception {
        // Given that there is an authenticated user,and it is their first time selecting preferences.
        User testUser = new User();

        given(authHelper.getAuthenticatedUser()).willReturn(testUser);
        UserPreferenceForm testForm = new UserPreferenceForm();

        // When the user does not select any preferences and submits the select preferences form.
        MvcResult when = mvc
                .perform(post("/select-preferences")
                        .with(csrf())
                        .flashAttr("userPreferenceForm", testForm))
                .andDo(print())

                // Then they are redirected to the create-workout page.
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/workout")).andReturn();

        then(userPreferenceService).should().selectPreferences(testUser, testForm);
        then(userPreferenceService).should().selectConditions(testUser, testForm);
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void shouldGetFormAfterInvalidFormSubmission() throws Exception {
        // Given that there is an authenticated user,and it is their first time selecting preferences.
        User testUser = new User();
        given(authHelper.getAuthenticatedUser()).willReturn(testUser);
        UserPreferenceForm testForm = new UserPreferenceForm();

        // When the user submits the preference form with an invalid preference ID.
        MvcResult when = mvc
                .perform(post("/select-preferences")
                        .with(csrf())
                        .flashAttr("userPreferenceForm", testForm)
                        .param("selectedPreferenceIds", "invalidPreference")
                )
                .andDo(print())

                // Then the select preferences form is returned to the user and form does not submit.
                .andExpect(status().isOk())
                .andExpect(view().name("conditions-preference/preference-form"))
                .andExpect(model().attributeHasFieldErrors("userPreferenceForm", "selectedPreferenceIds"))
                .andReturn();
    }


}
