package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.CityDTO;
import monsters.mapper.CityMapper;
import monsters.model.CityEntity;
import monsters.repository.CityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.persistence.EntityExistsException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    private static final String EXC_MES_ID = "none city was found by id";
    private static final String EXC_MES_NAME = "none city was found by name";
    private static final String EXC_EXIST = "city with this name already exists";

    public Mono<CityEntity> save(Mono<CityDTO> cityDTOMono) {
        return cityDTOMono.flatMap(cityDTO -> cityRepository.findByName(cityDTO.getName())
                .hasElements()
                .flatMap(hasElements -> hasElements ?
                        Mono.error(new EntityExistsException(EXC_EXIST + ": " + cityDTO.getName())) :
                        cityRepository.save(cityMapper.mapDtoToEntity(cityDTO))));
    }

    public Mono<CityEntity> findByName(Mono<String> cityNameMono) {
        return cityNameMono.flatMap(cityName -> cityRepository.findByName(cityName)
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_NAME + ": " + cityName)))
                .single());
    }

    public Mono<CityEntity> updateById(Mono<UUID> cityIdMono, Mono<CityDTO> cityDTOMono) {
        return cityIdMono.flatMap(cityId -> cityRepository.findById(cityId)
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + cityId)))
                .then(cityDTOMono.flatMap(cityDTO -> {
                    cityDTO.setId(cityId);
                    return cityRepository.save(cityMapper.mapDtoToEntity(cityDTO));
                })));
    }

    public Mono<Page<CityEntity>> findAll(Mono<Integer> page, Mono<Integer> size) {
        return page.zipWith(size)
                .flatMap(tuple -> cityRepository.findAll(PageRequest.of(tuple.getT1(), tuple.getT2())));
    }

    public void delete(Mono<UUID> cityIdMono) {
        cityIdMono.map(cityId -> cityRepository.findById(cityId)
                        .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + cityId)))
                        .then(cityRepository.deleteById(cityId)))
                .subscribe();
    }
}
