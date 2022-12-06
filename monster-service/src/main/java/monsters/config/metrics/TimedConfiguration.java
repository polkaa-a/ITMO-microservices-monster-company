package monsters.config.metrics;

import io.micrometer.core.aop.TimedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimedConfiguration {

    @Bean
    public TimedAspect timedAspect(){
        
    }
}
