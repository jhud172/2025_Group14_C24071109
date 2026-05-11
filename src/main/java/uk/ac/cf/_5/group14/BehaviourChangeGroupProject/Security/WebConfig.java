package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsLocaleInterceptor;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthHelper authHelper;
    private final UserSettingsService userSettingsService;

    public WebConfig(AuthHelper authHelper, UserSettingsService userSettingsService) {
        this.authHelper = authHelper;
        this.userSettingsService = userSettingsService;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/access-denied").setViewName("error/403");
        registry.addViewController("/confirm-logout").setViewName("auth/confirm-logout");

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userSettingsLocaleInterceptor());
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    @Bean
    public UserSettingsLocaleInterceptor userSettingsLocaleInterceptor() {
        return new UserSettingsLocaleInterceptor(authHelper, userSettingsService, localeResolver());
    }

}