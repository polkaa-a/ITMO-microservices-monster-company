package monsters.service.feign.clients;

import monsters.dto.InfectionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactivefeign.spring.config.ReactiveFeignClient;

import java.util.Date;
import java.util.List;

@ReactiveFeignClient(name = "infection-service", url = "http://localhost:8083/")
public interface InfectionServiceFeignClient {
    @GetMapping("/infections/{date}")
    @ResponseStatus(HttpStatus.OK)
    List<InfectionDTO> findAllByDate(@PathVariable Date date);
}
