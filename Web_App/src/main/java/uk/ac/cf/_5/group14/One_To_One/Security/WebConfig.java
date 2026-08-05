package uk.ac.cf._5.group14.One_To_One.Security;

import java.util.Locale;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsLocaleInterceptor;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthHelper authHelper;
    private final UserSettingsService userSettingsService;
    private final ObjectProvider<CurrentUserArgumentResolver> currentUserArgumentResolverProvider;
    private final String profileUploadLocation;
    private final String merchUploadLocation;

    public WebConfig(AuthHelper authHelper,
                     UserSettingsService userSettingsService,
                     ObjectProvider<CurrentUserArgumentResolver> currentUserArgumentResolverProvider,
                     @Value("${app.storage.profile-dir:uploads/profile}") String profileUploadDirectory,
                     @Value("${app.storage.merch-dir:uploads/merch}") String merchUploadDirectory) {
        this.authHelper = authHelper;
        this.userSettingsService = userSettingsService;
        this.currentUserArgumentResolverProvider = currentUserArgumentResolverProvider;
        this.profileUploadLocation = resourceLocation(profileUploadDirectory);
        this.merchUploadLocation = resourceLocation(merchUploadDirectory);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/home-public", "/");
        registry.addViewController("/favicon.ico").setViewName("redirect:/img/brand/logo.png");

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/profile/**")
                .addResourceLocations(profileUploadLocation);
        registry.addResourceHandler("/uploads/merch/**")
                .addResourceLocations(merchUploadLocation);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userSettingsLocaleInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        CurrentUserArgumentResolver resolver = currentUserArgumentResolverProvider.getIfAvailable();
        if (resolver != null) {
            resolvers.add(resolver);
        }
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

    private static String resourceLocation(String configuredDirectory) {
        String location = Path.of(configuredDirectory)
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();
        return location.endsWith("/") ? location : location + "/";
    }
}
