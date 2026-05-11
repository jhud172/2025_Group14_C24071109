package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfig {

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
