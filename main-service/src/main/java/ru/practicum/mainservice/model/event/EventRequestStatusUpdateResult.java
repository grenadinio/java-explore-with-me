package ru.practicum.mainservice.model.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.mainservice.model.request.ParticipationRequestDto;

import java.util.List;

@Getter
@Setter
@Builder
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}
