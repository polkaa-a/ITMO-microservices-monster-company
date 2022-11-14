package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.FearActionDTO;
import monsters.mapper.FearActionMapper;
import monsters.model.FearActionEntity;
import monsters.repository.FearActionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FearActionService {

    private final FearActionRepository fearActionRepository;
    private final FearActionMapper fearActionMapper;

    private static final String EXC_MES_ID = "none fear action was found by id";

    public Mono<FearActionEntity> save(Mono<FearActionDTO> fearActionDTOMono) {
        return fearActionMapper.mapDtoToEntity(fearActionDTOMono).flatMap(fearActionRepository::save);
    }

    public Mono<FearActionEntity> findById(Mono<UUID> fearActionIdMono) {
        return fearActionIdMono.flatMap(fearActionId -> fearActionRepository.findById(fearActionId)
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + fearActionId))));
    }

    public Mono<FearActionEntity> updateById(Mono<UUID> fearActionIdMono, Mono<FearActionDTO> fearActionDTOMono) {
        return fearActionIdMono.flatMap(fearActionId -> fearActionRepository.findById(fearActionId)
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + fearActionId)))
                .then(fearActionMapper.mapDtoToEntity(fearActionDTOMono).flatMap(fearActionEntity -> {
                    fearActionEntity.setId(fearActionId);
                    return fearActionRepository.save(fearActionEntity);
                })));
    }

    public void delete(Mono<UUID> fearActionIdMono) {
        fearActionIdMono.map(fearActionId -> fearActionRepository.findById(fearActionId)
                        .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + fearActionId)))
                        .then(fearActionRepository.deleteById(fearActionId)))
                .subscribe();
    }

}
