package ru.practicum.server.model;

import org.springframework.stereotype.Component;
import ru.practicum.dto.EndpointHitDto;

@Component
public class EndpointHitMapper {
    public EndpointHit toEndpointHit(EndpointHitDto dto) {
        return new EndpointHit(
                dto.getId(),
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp()
        );
    }
}
