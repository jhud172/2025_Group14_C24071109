//package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HomePageTests;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLogService;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HomePage.HomePageController;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleAppliedRepository;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleService;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
//
//import java.util.Collections;
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
//@AutoConfigureMockMvc(addFilters = false) // Bypass security filters for view rendering
//class BannerTest {
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
//
//    // User (Welcome Banner)
//    @Test
//    @WithMockUser(username = "FitnessFan", roles = "USER")
//    void shouldShowWelcomeAndQuickActions_WhenLoggedIn() throws Exception {
//
//        User mockUser = new User();
//        mockUser.setUsername("FitnessFan");
//
//        when(authHelper.getAuthenticatedUser()).thenReturn(mockUser);
//
//        when(scheduleAppliedRepository.findByUser(any(User.class))).thenReturn(Collections.emptyList());
//        when(exerciseLogService.findTop5RecentExerciseLogs(any(User.class))).thenReturn(Collections.emptyList());
//
//        mockMvc.perform(get("/"))
//                .andExpect(status().isOk())
//                // Verify Welcome Message
//                .andExpect(content().string(containsString("Welcome back")))
//                .andExpect(content().string(containsString("FitnessFan")))
//
//                // Verify Encouragement Text
//                .andExpect(content().string(containsString("great progress")))
//
//                // Verify The NEW Quick Actions are present in the banner
//                .andExpect(content().string(containsString("Start Workout")))
//                .andExpect(content().string(containsString("View Progress")))
//                .andExpect(content().string(containsString("Edit Schedule")))
//                .andExpect(content().string(containsString("GP Connection")))
//
//                // Verify Guest Content is HIDDEN
//                .andExpect(content().string(not(containsString("Ready to Improve your Lifestyle?"))))
//                .andExpect(content().string(not(containsString("Start Now"))));
//    }
//
//    // Guest (Intro Banner)
//    @Test
//    void shouldShowIntroBanner_WhenNotLoggedIn() throws Exception {
//
//        when(authHelper.getAuthenticatedUser()).thenReturn(null);
//
//        mockMvc.perform(get("/"))
//                .andExpect(status().isOk())
//                // Verify Intro Text
//                .andExpect(content().string(containsString("Ready to Improve your Lifestyle?")))
//                .andExpect(content().string(containsString("unlock your healthy lifestyle glow")))
//
//                // Verify Start Button is present
//                .andExpect(content().string(containsString("Start Now")))
//
//                // Verify Logged-In Content is HIDDEN
//                .andExpect(content().string(not(containsString("Welcome back"))))
//                .andExpect(content().string(not(containsString("Quick Actions"))))
//                .andExpect(content().string(not(containsString("GP Connection"))));
//    }
//}