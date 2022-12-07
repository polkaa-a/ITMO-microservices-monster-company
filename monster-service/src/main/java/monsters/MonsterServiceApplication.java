package monsters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableReactiveFeignClients
public class MonsterServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonsterServiceApplication.class, args);
    }

}
