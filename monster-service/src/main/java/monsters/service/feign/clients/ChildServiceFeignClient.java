package monsters.service.feign.clients;

import monsters.dto.DoorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@FeignClient(name = "child-service")
public interface ChildServiceFeignClient {
    @GetMapping("/doors/{id}")
    @ResponseStatus(HttpStatus.OK)
    DoorDTO findById(@PathVariable UUID id);
}
