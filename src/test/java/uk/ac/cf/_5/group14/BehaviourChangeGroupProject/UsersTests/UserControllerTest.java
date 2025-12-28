package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UsersTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // <--- NEW IMPORT
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserController;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private javax.sql.DataSource dataSource;

    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    void showSignupFormToReturnSignupView() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("User/signup"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void signupUserToRedirectToLoginWhenValid() throws Exception {

        when(userService.emailExists(anyString())).thenReturn(false);
        when(userService.usernameExists(anyString())).thenReturn(false);

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@test.com")
                        .param("username", "johndoe")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        verify(userService).saveUser(any(User.class));
    }

    @Test
    void signupUserToReturnViewIfEmailExists() throws Exception {

        when(userService.emailExists("john@test.com")).thenReturn(true);

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@test.com")
                        .param("username", "johndoe")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("User/signup"))
                .andExpect(model().attributeExists("error"));

        verify(userService, never()).saveUser(any(User.class));
    }
}