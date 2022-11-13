package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.CityDTO;
import monsters.dto.PageDTO;
import monsters.mapper.CityMapper;
import monsters.mapper.PageMapper;
import monsters.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cities")
@PreAuthorize("hasAuthority('ADMIN')")
public class CityController {

    private final CityService cityService;
    private final CityMapper cityMapper;
    private final PageMapper<CityDTO> pageMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CityDTO> addCity(@Valid @RequestBody Mono<CityDTO> cityDTOMono) {
        return cityService.save(cityDTOMono).map(cityMapper::mapEntityToDto);
    }

    @GetMapping
    public Mono<ResponseEntity<PageDTO<CityDTO>>> findAll(@RequestParam(defaultValue = "0")
                                                          @Min(value = 0, message = "must not be less than zero") Mono<Integer> page,
                                                          @RequestParam(defaultValue = "5")
                                                          @Max(value = 50, message = "must not be more than 50 characters") Mono<Integer> size) {

        var pagesMono = cityService.findAll(page, size);
        var emptyResponseMono = Mono.just(new ResponseEntity<PageDTO<CityDTO>>(HttpStatus.NO_CONTENT));
        var pagesDTOMono = pagesMono.map(pageOfCity -> pageOfCity.map(cityMapper::mapEntityToDto)).map(pageMapper::mapToDto);

        return pagesMono.flatMap(list -> list.isEmpty() ? emptyResponseMono : pagesDTOMono.map(pagesDTO -> new ResponseEntity<>(pagesDTO, HttpStatus.OK)));
    }

    @DeleteMapping("/{cityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(@PathVariable Mono<UUID> cityId) {
        cityService.delete(cityId);
    }

    @PutMapping("/{cityId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CityDTO> putCity(@PathVariable Mono<UUID> cityId, @Valid @RequestBody Mono<CityDTO> cityDTOMono) {
        return cityService.updateById(cityId, cityDTOMono).map(cityMapper::mapEntityToDto);
    }
}

