package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("/User/login");
        registry.addViewController("/access-denied").setViewName("error/403");
        registry.addViewController("/confirm-logout").setViewName("confirm-logout");
        registry.addViewController("/logout").setViewName("redirect:/confirm-logout");

    }

}