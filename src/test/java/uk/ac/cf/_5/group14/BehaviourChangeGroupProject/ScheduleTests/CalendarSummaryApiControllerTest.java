package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayMode.DayModeService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Goals.GoalLinkService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.CalendarSummaryApiController;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.CalendarSummaryService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.CalendarSummaryService.CalendarDaySummary;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalendarSummaryApiController.class)
@ActiveProfiles("test")
class CalendarSummaryApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CalendarSummaryService summaryService;

    @MockitoBean
    private AuthHelper authHelper;

    @MockitoBean
    private DayModeService dayModeService;

    @MockitoBean
    private GoalLinkService goalLinkService;

    @MockitoBean
    private UserSettingsService userSettingsService;

    @Test
    void summaryReturnsRangeDetails() throws Exception {
        User user = new User();
        user.setId(7L);

        given(authHelper.getAuthenticatedUser()).willReturn(user);
        given(summaryService.getSummary(eq(user), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(List.of(
                        new CalendarDaySummary(LocalDate.of(2026, 2, 1), 3, true, false, 0),
                        new CalendarDaySummary(LocalDate.of(2026, 2, 2), 0, false, true, 1)
                ));

        mvc.perform(get("/api/calendar/summary")
                        .param("start", "2026-02-01")
                        .param("end", "2026-02-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2026-02-01"))
                .andExpect(jsonPath("$[0].loadScore").value(3))
                .andExpect(jsonPath("$[0].hasWorkout").value(true))
                .andExpect(jsonPath("$[0].hasNutrition").value(false))
                .andExpect(jsonPath("$[0].prHitCount").value(0))
                .andExpect(jsonPath("$[1].date").value("2026-02-02"))
                .andExpect(jsonPath("$[1].hasNutrition").value(true))
                .andExpect(jsonPath("$[1].prHitCount").value(1));
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }

        @Bean
        public Clock systemClock() {
            return Clock.systemDefaultZone();
        }

        @Bean
        public AuthHelper authHelper() {
            return mock(AuthHelper.class);
        }

        @Bean
        public PlatformSubscriptionService platformSubscriptionService() {
            return mock(PlatformSubscriptionService.class);
        }
    }
}
