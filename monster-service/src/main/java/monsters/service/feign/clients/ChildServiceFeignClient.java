package monsters.service.feign.clients;

import monsters.dto.DoorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactivefeign.spring.config.ReactiveFeignClient;

import java.util.UUID;

@ReactiveFeignClient(name = "infection-service")
public interface ChildServiceFeignClient {
    @GetMapping("/doors/{id}")
    @ResponseStatus(HttpStatus.OK)
    DoorDTO findById(@PathVariable UUID id);
}
