package ru.practicum.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.model.EndpointHitMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class ServerService {
    private final ServerRepository serverRepository;
    private final EndpointHitMapper hitMapper;

    @Transactional(readOnly = true)
    public List<ViewStats> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала (" + start + ") должна быть раньше даты конца (" + end + ").");
        }
        if (unique) {
            return serverRepository.findByParamsUniqueIp(start, end, uris);
        } else {
            return serverRepository.findByParams(start, end, uris);
        }
    }

    @Transactional
    public EndpointHit saveHit(EndpointHitDto hit) {
        return serverRepository.save(hitMapper.toEndpointHit(hit));
    }
}
