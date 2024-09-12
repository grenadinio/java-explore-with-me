package ru.practicum.mainservice.model.request;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class RequestsMapper {
    public ParticipationRequestDto toRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }
}
