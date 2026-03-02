package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.session.InvalidSessionStrategy;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Config.DevModeProperties;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private DevModeProperties devModeProperties;

    private static final String[] ENDPOINTS_WHITELIST = {
            "/img/**",
            "/css/**",
        "/js/**",
        "/webjars/**",
        "/favicon.ico",
        "/static/**",
            "/uploads/**",
            "/",
            "/home-public",
            "/about",
            "/pricing",
            "/pricing/**",
            "/explore",
            "/merch",
            "/u/**",
            "/error/**",
            "/login/**",
            "/dev-mode/**",
            "/access-denied/**",
                "/signup/**",
                "/forgot-password",
                "/reset-password",
                    "/policies/**",
                    "/verify/email",
                    "/verify/email/code",
                    "/verify/email/confirm",
                    "/verify/phone/code"
    };
    
    /*
     * DEVELOPMENT MODE:
     * When DEV_MODE environment variable is set to "true":
     * - GET /login is redirected to /login-demo by HomeController
     * - Home page displays development mode indicator
     * - Login functionality is disabled (no authentication required)
     * 
     * To enable dev mode:
     * 1. Set environment variable: DEV_MODE=true
     * 2. Restart the application
     * 3. All login endpoints will show demo page instead of actual login form
     * 
     * To disable dev mode:
     * 1. Set environment variable: DEV_MODE=false (or unset)
     * 2. Restart the application
     * 3. Normal Spring Security login flow will be activated
     */

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   LoginThrottleFilter loginThrottleFilter,
                                                   CustomAuthenticationFailureHandler failureHandler,
                                                   CustomAuthenticationSuccessHandler successHandler,
                                                   LogoutHandler logoutHandler,
                                                   AccessDeniedHandler accessDeniedHandler) throws Exception {
        http
                .authorizeHttpRequests(request -> {
                        if (devModeProperties.isDevMode()) {
                            request.requestMatchers("/profile/**").permitAll();
                        }
                        request
                        .requestMatchers(ENDPOINTS_WHITELIST).permitAll()
                        .requestMatchers("/confirm-logout").authenticated()
                .requestMatchers("/trainer/**").hasRole("TRAINER")
                .requestMatchers("/gym/**").hasRole("GYM_ADMIN")
                .requestMatchers("/client/**").hasAnyRole("CLIENT", "USER")
                .requestMatchers("/trainers/**").hasAnyRole("CLIENT", "USER")
                .requestMatchers("/admin/**").hasAnyRole("PLATFORM_ADMIN", "SUPER_ADMIN")
                .requestMatchers("/merch/**").authenticated()
                .requestMatchers("/dashboard").authenticated()
                        .anyRequest().authenticated();
                })

                .formLogin(form -> form.loginPage("/login")
                    .permitAll()
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .failureHandler(failureHandler)
                    .successHandler(successHandler))

                .logout((l) -> l
                    .logoutUrl("/logout")
                    .addLogoutHandler(logoutHandler)
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID"))
                .sessionManagement(session -> session
                    .invalidSessionStrategy(invalidSessionStrategy()))
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                request -> request.getRequestURI() != null
                                        && request.getRequestURI().startsWith("/chat/")
                                        && (HttpMethod.GET.matches(request.getMethod())
                                        || HttpMethod.POST.matches(request.getMethod()))
                    ));

            http.addFilterBefore(loginThrottleFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public InvalidSessionStrategy invalidSessionStrategy() {
        return new CustomInvalidSessionStrategy();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public LogoutHandler logoutHandler() {
        return new CustomLogoutHandler();
    }


}
