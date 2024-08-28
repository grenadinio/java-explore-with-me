package ru.practicum.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerService {
    private final ServerRepository serverRepository;

    @Transactional(readOnly = true)
    public List<ViewStats> getStatistics(Timestamp start, Timestamp end, List<String> uris, boolean unique) {
        if (unique) {
            return serverRepository.findByParamsUniqueIp(start, end, uris);
        } else {
            return serverRepository.findByParams(start, end, uris);
        }
    }

    @Transactional
    public EndpointHit saveHit(EndpointHit hit) {
        return serverRepository.save(hit);
    }
}
