package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotAllowedException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.event.Event;
import ru.practicum.mainservice.model.request.ParticipationRequestDto;
import ru.practicum.mainservice.model.request.Request;
import ru.practicum.mainservice.model.request.RequestsMapper;
import ru.practicum.mainservice.model.user.User;
import ru.practicum.mainservice.repository.EventsRepository;
import ru.practicum.mainservice.repository.RequestsRepository;
import ru.practicum.mainservice.repository.UsersRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestsService {
    private final RequestsRepository requestsRepository;
    private final UsersRepository usersRepository;
    private final EventsRepository eventsRepository;
    private final RequestsMapper requestsMapper;

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventsRequestsByUserId(Long userId) {
        return requestsRepository
                .findByRequesterId(userId)
                .stream()
                .map(requestsMapper::toRequestDto)
                .toList();
    }

    public ParticipationRequestDto createUserEventRequest(Long userId, Long eventId) {
        User user = validateAndGetUser(userId);
        Event event = validateAndGetEvent(eventId);
        List<Request> userRequests = requestsRepository.findByRequesterId(userId);
        long requestAmountForEvent = requestsRepository.countByEventIdAndStatus(event.getId(), "CONFIRMED");

        for (Request request : userRequests) {
            if (Objects.equals(request.getEvent().getId(), eventId)) {
                throw new ConflictException("Request with id = " + request.getId() + " is duplicate");
            }
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Could not add request to event created by yourself");
        }

        if (event.getState().equals("PENDING") || event.getState().equals("CANCELED")) {
            throw new ConflictException("Event with id = " + event.getId() + " is not published");
        }

        if ((event.getParticipantLimit() != 0) && (event.getParticipantLimit() < (requestAmountForEvent + 1))) {
            throw new ConflictException("Exceeded max requesters amount");
        }

        Request request = new Request();
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus("CONFIRMED");
        } else {
            request.setStatus("PENDING");
        }

        return requestsMapper.toRequestDto(requestsRepository.save(request));
    }

    public ParticipationRequestDto cancelUserEventRequest(Long userId, Long requestId) {
        Request request = validateAndGetRequest(requestId);
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new NotAllowedException("You are not allowed to cancel request with id = " + requestId);
        }
        request.setStatus("CANCELED");
        return requestsMapper.toRequestDto(requestsRepository.save(request));
    }

    private Request validateAndGetRequest(long requestId) {
        Optional<Request> request = requestsRepository.findById(requestId);

        if (request.isEmpty()) {
            throw new NotFoundException("Request with id = " + request + " was not found");
        }
        return request.get();
    }

    private User validateAndGetUser(Long userId) {
        Optional<User> user = usersRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User with id = " + userId + " was not found");
        }
        return user.get();
    }

    private Event validateAndGetEvent(Long eventId) {
        Optional<Event> event = eventsRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new NotFoundException("Event with id = " + eventId + " was not found");
        }
        return event.get();
    }
}
