package uk.ac.cf._5.group14.One_To_One.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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

import uk.ac.cf._5.group14.One_To_One.Config.DevModeProperties;
import uk.ac.cf._5.group14.One_To_One.DevMode.DevModePageRestrictionFilter;

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
            "/faq",
            "/pricing",
            "/pricing/**",
            "/explore",
            "/merch",
            "/support/feedback",
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
                    "/verify/email/send",
                    "/verify/email/confirm",
                    "/verify/phone/code"
    };
    
    /*
     * DEVELOPMENT MODE:
     * When DEV_MODE environment variable is set to "true":
     * - GET /login renders the dev-mode landing page (login-demo.html)
     * - Home page displays a development mode indicator badge
     * - Public pages are freely accessible without login
     * - Auth pages (dashboard, calendar, workouts, goals, profile) still require login
     *   but are fully functional once the user signs in via /login?devLogin=1
     *
     * To enable dev mode:
     * 1. Set environment variable: DEV_MODE=true
     * 2. Restart the application
     *
     * To disable dev mode:
     * 1. Set environment variable: DEV_MODE=false (or unset)
     * 2. Restart the application
     */

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/img/**",
                "/css/**",
                "/js/**",
                "/webjars/**",
                "/favicon.ico",
                "/static/**",
                "/uploads/**");
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   LoginThrottleFilter loginThrottleFilter,
                                                   DevModePageRestrictionFilter devModePageRestrictionFilter,
                                                   CustomAuthenticationFailureHandler failureHandler,
                                                   CustomAuthenticationSuccessHandler successHandler,
                                                   LogoutHandler logoutHandler,
                                                   AccessDeniedHandler accessDeniedHandler) throws Exception {
        http
                .authorizeHttpRequests(request -> {
                        if (devModeProperties.isDevMode()) {
                            // DEV MODE: Public pages are freely accessible without login.
                            // Authenticated pages (dashboard, calendar, workouts, goals, profile)
                            // require login but are fully functional once signed in.
                            // Restricted sections keep the same auth/role rules as production.
                            request
                            // Static assets and public pages: always open
                            .requestMatchers(ENDPOINTS_WHITELIST).permitAll()
                            .requestMatchers("/dashboard/public", "/client/dashboard/public").permitAll()
                            // Leaderboard: keep protected â€” not open in dev mode
                            .requestMatchers("/levels/**").authenticated()
                            // Trainers area: keep role requirements
                            .requestMatchers("/trainer/**").hasRole("TRAINER")
                            .requestMatchers("/gym/**").hasRole("GYM_ADMIN")
                            .requestMatchers("/client/trainers", "/client/trainers/**").hasAnyRole("CLIENT", "USER")
                            .requestMatchers("/client/messages", "/client/messages/**").authenticated()
                            .requestMatchers("/admin/dashboard", "/admin/feedback", "/admin/feedback/**", "/admin/outreach/**", "/admin/dev-pages/**")
                            .hasAnyRole("GYM_ADMIN", "PLATFORM_ADMIN", "SUPER_ADMIN")
                            .requestMatchers("/trainers/**").hasAnyRole("CLIENT", "USER")
                            // Training Vault: keep protected
                            .requestMatchers("/vault/**").authenticated()
                            // Core authenticated pages: require login in dev mode
                            .requestMatchers("/dashboard", "/dashboard/**", "/client/dashboard", "/client/dashboard/**").authenticated()
                            .requestMatchers("/calendar", "/calendar/**").authenticated()
                            .requestMatchers("/workouts", "/workouts/**").authenticated()
                            .requestMatchers("/workout-session", "/workout-session/**").authenticated()
                            .requestMatchers("/workout-management", "/workout-management/**").authenticated()
                            .requestMatchers("/goals", "/goals/**").authenticated()
                            .requestMatchers("/profile", "/profile/**").authenticated()
                            .requestMatchers("/merch/**").authenticated()
                            // All other routes: open for dev browsing
                            .anyRequest().permitAll();
                        } else {
                            // Normal mode: keep existing security configuration unchanged.
                            request
                            .requestMatchers(ENDPOINTS_WHITELIST).permitAll()
                            .requestMatchers("/dashboard/public", "/client/dashboard/public").permitAll()
                            .requestMatchers("/confirm-logout").authenticated()
                            .requestMatchers("/trainer/**").hasRole("TRAINER")
                            .requestMatchers("/gym/**").hasRole("GYM_ADMIN")
                            .requestMatchers("/client/**").hasAnyRole("CLIENT", "USER")
                            .requestMatchers("/trainers/**").hasAnyRole("CLIENT", "USER")
                            .requestMatchers("/admin/dashboard", "/admin/feedback", "/admin/feedback/**", "/admin/outreach/**", "/admin/dev-pages/**")
                            .hasAnyRole("GYM_ADMIN", "PLATFORM_ADMIN", "SUPER_ADMIN")
                            .requestMatchers("/admin/**").hasAnyRole("PLATFORM_ADMIN", "SUPER_ADMIN")
                            .requestMatchers("/merch/**").authenticated()
                            .requestMatchers("/dashboard", "/dashboard/**", "/client/dashboard", "/client/dashboard/**").authenticated()
                            .requestMatchers("/calendar", "/calendar/**").authenticated()
                            .requestMatchers("/workouts", "/workouts/**").authenticated()
                            .requestMatchers("/workout-session", "/workout-session/**").authenticated()
                            .requestMatchers("/workout-management", "/workout-management/**").authenticated()
                            .requestMatchers("/goals", "/goals/**").authenticated()
                            .requestMatchers("/profile", "/profile/**").authenticated()
                            .anyRequest().authenticated();
                        }
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
            http.addFilterBefore(devModePageRestrictionFilter, UsernamePasswordAuthenticationFilter.class);
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
