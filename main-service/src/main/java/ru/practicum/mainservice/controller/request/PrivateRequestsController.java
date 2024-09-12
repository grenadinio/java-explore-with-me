package ru.practicum.mainservice.controller.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.model.request.ParticipationRequestDto;
import ru.practicum.mainservice.service.RequestsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class PrivateRequestsController {
    private final RequestsService requestsService;

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getEventsRequestsByUserId(@PathVariable Long userId) {
        return requestsService.getEventsRequestsByUserId(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createUserEventRequest(@PathVariable Long userId,
                                                          @RequestParam Long eventId) {
        return requestsService.createUserEventRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelUserEventRequest(@PathVariable Long userId,
                                                          @PathVariable Long requestId) {
        return requestsService.cancelUserEventRequest(userId, requestId);
    }
}
