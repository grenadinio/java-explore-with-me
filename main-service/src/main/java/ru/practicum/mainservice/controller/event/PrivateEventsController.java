package ru.practicum.mainservice.controller.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.model.event.EventFullDto;
import ru.practicum.mainservice.model.event.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.model.event.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.model.event.EventShortDto;
import ru.practicum.mainservice.model.event.NewEventDto;
import ru.practicum.mainservice.model.event.UpdateEventRequest;
import ru.practicum.mainservice.model.request.ParticipationRequestDto;
import ru.practicum.mainservice.service.EventsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
public class PrivateEventsController {
    private final EventsService eventsService;

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        return eventsService.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createUserEvent(@PathVariable Long userId,
                                        @Valid @RequestBody NewEventDto body) {
        return eventsService.createUserEvent(userId, body);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         HttpServletRequest request) {
        return eventsService.getUserEventById(userId, eventId, request.getRequestURI());
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEventById(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @RequestBody @Valid UpdateEventRequest body) {
        return eventsService.updateUserEventById(userId, eventId, body);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequestsById(@PathVariable Long userId,
                                                                  @PathVariable Long eventId) {
        return eventsService.updateUserEventRequestsById(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateUserEventRequestById(@PathVariable Long userId,
                                                                     @PathVariable Long eventId,
                                                                     @RequestBody @Valid EventRequestStatusUpdateRequest body) {
        return eventsService.updateUserEventRequestById(userId, eventId, body);
    }
}
