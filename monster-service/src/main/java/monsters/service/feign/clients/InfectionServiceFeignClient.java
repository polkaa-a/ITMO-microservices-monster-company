package monsters.service.feign.clients;

import monsters.dto.InfectionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;
import java.util.List;

@FeignClient(name = "infection-service")
public interface InfectionServiceFeignClient {
    @GetMapping("/infections/{date}")
    @ResponseStatus(HttpStatus.OK)
    List<InfectionDTO> findAllByDate(@PathVariable Date date);
}
