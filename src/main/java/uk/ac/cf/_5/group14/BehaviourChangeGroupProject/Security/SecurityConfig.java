package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security;

import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] ENDPOINTS_WHITELIST = {
            "/img/**",
            "/css/**",
            "/",
            "/error/**",
            "/login/**",
            "/access-denied/**",
            "/signup/**"
    };

    @Autowired
    private DataSource dataSource;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers(ENDPOINTS_WHITELIST).permitAll()
                        .requestMatchers("/confirm-logout").authenticated()
                        .anyRequest().authenticated())

                .formLogin(form -> form.loginPage("/login")
                        .permitAll()
                        .failureHandler(authenticationFailureHandler())
                        .successHandler(authenticationSuccessHandler()))

                .logout((l) -> l.addLogoutHandler(logoutHandler()))
                .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler()));
        return http.build();
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    private LogoutHandler logoutHandler() {
        return new CustomLogoutHandler();
    }

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return successHandler;
    }

    private AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        JdbcDaoImpl userDetailsDao = new JdbcDaoImpl();
        userDetailsDao.setDataSource(dataSource);
        userDetailsDao.setUsersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username = ?");
        userDetailsDao.setAuthoritiesByUsernameQuery("SELECT username, authority FROM user_authorities WHERE username = ?");
        return userDetailsDao;

    }
    public SecurityConfig() {
        System.out.println("!!! SECURITY CONFIG IS LOADING !!!");
    }

}
