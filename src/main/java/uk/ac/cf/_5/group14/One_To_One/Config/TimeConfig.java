package uk.ac.cf._5.group14.One_To_One.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.Clock;

@Configuration
@EnableAsync
public class TimeConfig {

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
