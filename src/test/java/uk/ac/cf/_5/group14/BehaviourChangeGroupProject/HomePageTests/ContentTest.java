//package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HomePageTests;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLog;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLogService;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HomePage.HomePageController;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.Schedule;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleApplied;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleAppliedRepository;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleService;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.hamcrest.Matchers.containsString;
//import static org.hamcrest.Matchers.not;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(HomePageController.class)
//@AutoConfigureMockMvc(addFilters = false) // Bypasses security filters to focus on content rendering
//class ContentTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean private ScheduleAppliedRepository scheduleAppliedRepository;
//    @MockitoBean private ExerciseLogService exerciseLogService;
//    @MockitoBean private AuthHelper authHelper;
//    @MockitoBean private ScheduleService scheduleService;
//
//    @MockitoBean private javax.sql.DataSource dataSource;
//    @MockitoBean private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
//
//    // User (Dashboard View)
//    @Test
//    @WithMockUser(username = "TestUser", roles = "USER")
//    void shouldShowDashboard_WhenLoggedIn() throws Exception {
//
//        User mockUser = new User();
//        mockUser.setUsername("TestUser");
//        when(authHelper.getAuthenticatedUser()).thenReturn(mockUser);
//
//        Schedule schedule = new Schedule();
//        schedule.setName("Summer Body Plan");
//        schedule.setDescription("High intensity cardio");
//
//        ScheduleApplied applied = new ScheduleApplied();
//        applied.setSchedule(schedule);
//        applied.setDateApplied(LocalDate.now());
//
//        when(scheduleAppliedRepository.findByUser(any(User.class)))
//                .thenReturn(List.of(applied));
//
//        ExerciseLog log = new ExerciseLog();
//        log.setDate(LocalDate.now());
//        log.setConfidence(4);
//        log.setMoodBefore(3);
//        log.setMoodAfter(4);
//
//        when(exerciseLogService.findTop5RecentExerciseLogs(any(User.class)))
//                .thenReturn(List.of(log));
//
//        mockMvc.perform(get("/"))
//                .andExpect(status().isOk())
//                // Verify Dashboard Header
//                .andExpect(content().string(containsString("Feeling the Progress yet?")))
//                // Verify Schedule Data renders
//                .andExpect(content().string(containsString("Summer Body Plan")))
//                .andExpect(content().string(containsString("Your Schedule")))
//                // Verify Exercise Log Data renders
//                .andExpect(content().string(containsString("Confidence: 4/4")))
//                // Verify Quick Actions
//                .andExpect(content().string(containsString("Quick Actions")))
//                // Verify GUEST content is HIDDEN
//                .andExpect(content().string(not(containsString("How Our Exercise App Transforms Your Lifestyle"))))
//                .andExpect(content().string(not(containsString("Book GP Consultation"))));
//    }
//
//    // Guest (Marketing View)
//    @Test
//    void shouldShowMarketingPage_WhenNotLoggedIn() throws Exception {
//        when(authHelper.getAuthenticatedUser()).thenReturn(null);
//
//        mockMvc.perform(get("/"))
//                .andExpect(status().isOk())
//                // Verify Guest Header
//                .andExpect(content().string(containsString("How Our Exercise App Transforms Your Lifestyle")))
//                // Verify Specific Benefits
//                .andExpect(content().string(containsString("Visual Progress Tracking")))
//                .andExpect(content().string(containsString("Medical Integration")))
//                // Verify CTA Button
//                .andExpect(content().string(containsString("Book GP Consultation")))
//                // Verify LOGGED IN content is HIDDEN
//                .andExpect(content().string(not(containsString("Feeling the Progress yet?"))))
//                .andExpect(content().string(not(containsString("Summer Body Plan"))));
//    }
//}