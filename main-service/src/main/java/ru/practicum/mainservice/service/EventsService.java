package ru.practicum.mainservice.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.mainservice.ConnectToStatServer;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.exception.ValidationException;
import ru.practicum.mainservice.model.category.Category;
import ru.practicum.mainservice.model.event.Event;
import ru.practicum.mainservice.model.event.EventFullDto;
import ru.practicum.mainservice.model.event.EventMapper;
import ru.practicum.mainservice.model.event.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.model.event.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.model.event.EventShortDto;
import ru.practicum.mainservice.model.event.NewEventDto;
import ru.practicum.mainservice.model.event.UpdateEventRequest;
import ru.practicum.mainservice.model.request.EventIdByRequestsCount;
import ru.practicum.mainservice.model.request.ParticipationRequestDto;
import ru.practicum.mainservice.model.request.Request;
import ru.practicum.mainservice.model.request.RequestsMapper;
import ru.practicum.mainservice.model.user.User;
import ru.practicum.mainservice.repository.CategoriesRepository;
import ru.practicum.mainservice.repository.EventsRepository;
import ru.practicum.mainservice.repository.LocationsRepository;
import ru.practicum.mainservice.repository.RequestsRepository;
import ru.practicum.mainservice.repository.UsersRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventsService {
    private final EventsRepository repository;
    private final CategoriesRepository categoriesRepository;
    private final LocationsRepository locationRepository;
    private final RequestsRepository requestsRepository;
    private final UsersRepository usersRepository;
    private final EventMapper eventMapper;
    private final RequestsMapper requestsMapper;
    private final StatsClient statisticClient;

    private static final LocalDateTime MAX_DATETIME = LocalDateTime.of(3000, 12, 31, 23, 59, 59);
    private static final LocalDateTime MIN_DATETIME = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<EventFullDto> getAllEventsAdmin(List<Long> users, List<String> states, List<Long> categories,
                                                String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime start = rangeStart == null ? MIN_DATETIME : LocalDateTime.parse(rangeStart, DATE_FORMATTER);
        LocalDateTime end = rangeEnd == null ? MAX_DATETIME : LocalDateTime.parse(rangeEnd, DATE_FORMATTER);
        validateDates(start, end);
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size);

        List<EventFullDto> eventRespFulls = repository
                .findByConditionals(states, categories, users, start, end, pageable)
                .stream()
                .map(eventMapper::toEventFullDto)
                .toList();

        List<Long> eventsIds = eventRespFulls
                .stream()
                .map(EventFullDto::getId)
                .toList();

        Map<Long, Long> confirmedRequestsByEvents = requestsRepository
                .countByEventIdInAndStatusGroupByEvent(eventsIds, "CONFIRMED")
                .stream()
                .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));

        List<Long> views = ConnectToStatServer.getViews(MIN_DATETIME, MAX_DATETIME,
                ConnectToStatServer.prepareUris(eventsIds), true, statisticClient);

        for (int i = 0; i < eventRespFulls.size(); i++) {

            if ((!views.isEmpty()) && (views.get(i) != 0)) {
                eventRespFulls.get(i).setViews(views.get(i));
            } else {
                eventRespFulls.get(i).setViews(0L);
            }
            eventRespFulls.get(i)
                    .setConfirmedRequests(confirmedRequestsByEvents
                            .getOrDefault(eventRespFulls.get(i).getId(), 0L));
        }
        return eventRespFulls;
    }

    public EventFullDto updateEventAdmin(Long eventId, UpdateEventRequest updateRequest) {
        Event event = validateAndGetEvent(eventId);
        Category category = event.getCategory();

        if (updateRequest.getEventDate() != null && LocalDateTime.parse(updateRequest.getEventDate(), DATE_FORMATTER).isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start of event must be in future");
        }

        if (Duration.between(LocalDateTime.now(), event.getEventDate()).toHours() < 1) {
            throw new ConflictException("Start of event must be not less than in hour");
        }

        if (updateRequest.getStateAction() != null) {
            if (updateRequest.getStateAction().equals("PUBLISH_EVENT") && !event.getState().equals("PENDING")) {
                throw new ConflictException("State must be PENDING");
            }

            if (updateRequest.getStateAction().equals("REJECT_EVENT") && event.getState().equals("PUBLISHED")) {
                throw new ConflictException("States must be PENDING or CANCELED");
            }
        }
        if (updateRequest.getCategory() != null) {
            category = validateAndGetCategory(updateRequest.getCategory());
        }

        if (updateRequest.getLocation() != null) {
            locationRepository.save(updateRequest.getLocation());
        }

        Event updatedEvent = repository.save(updateEvent(event, updateRequest, category));
        long confirmedRequests = requestsRepository
                .countByEventIdAndStatus(eventId, "CONFIRMED");
        EventFullDto eventFull = eventMapper.toEventFullDto(updatedEvent);
        eventFull.setConfirmedRequests(confirmedRequests);
        return eventFull;
    }

    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size);

        List<EventShortDto> events = repository.findByInitiatorId(userId, pageable)
                .stream()
                .map(eventMapper::toEventShortDto)
                .toList();

        List<Long> eventIds = events.stream().map(EventShortDto::getId).toList();

        Map<Long, Long> confirmedRequestsByEvents = requestsRepository
                .countByEventIdInAndStatusGroupByEvent(eventIds, "CONFIRMED")
                .stream()
                .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));

        List<Long> views = ConnectToStatServer.getViews(MIN_DATETIME, MAX_DATETIME,
                ConnectToStatServer.prepareUris(eventIds), true, statisticClient);

        for (int i = 0; i < events.size(); i++) {

            if ((!views.isEmpty()) && (views.get(i) != 0)) {
                events.get(i).setViews(views.get(i));
            } else {
                events.get(i).setViews(0L);
            }
            events.get(i)
                    .setConfirmedRequests(confirmedRequestsByEvents
                            .getOrDefault(events.get(i).getId(), 0L));
        }
        return events;
    }

    public EventFullDto createUserEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }

        validateEventDate(LocalDateTime.parse(newEventDto.getEventDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        locationRepository.save(newEventDto.getLocation());

        Category category = categoriesRepository.findById(newEventDto.getCategory()).get();

        Event addingEvent = eventMapper.toEvent(newEventDto, category);
        addingEvent.setCreatedOn(LocalDateTime.now());
        addingEvent.setInitiator(validateAndGetUser(userId));
        addingEvent.setCategory(validateAndGetCategory(newEventDto.getCategory()));
        addingEvent.setCreatedOn(LocalDateTime.now());
        addingEvent.setState("PENDING");

        Event saved = repository.save(addingEvent);
        return eventMapper.toEventFullDto(saved);
    }

    public EventFullDto getUserEventById(Long userId, Long eventId, String path) {
        Event event = validateAndGetEvent(eventId);
        long confirmedRequests = requestsRepository
                .countByEventIdAndStatus(eventId, "CONFIRMED");
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(confirmedRequests);
        List<Long> views = ConnectToStatServer.getViews(MIN_DATETIME,
                MAX_DATETIME, path, true, statisticClient);
        if (views.isEmpty()) {
            eventFullDto.setViews(0L);
            return eventFullDto;
        }
        eventFullDto.setViews(views.getFirst());
        return eventFullDto;
    }

    public EventFullDto updateUserEventById(Long userId, Long eventId, UpdateEventRequest updateEventRequest) {
        Event updatingEvent = validateAndGetEvent(eventId);
        checkAbilityToUpdate(updatingEvent);

        if (updateEventRequest.getEventDate() != null) {
            validateEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        Category category = updatingEvent.getCategory();
        if (updateEventRequest.getCategory() != null) {
            category = validateAndGetCategory(updateEventRequest.getCategory());
        }

        Event updatedEvent = repository.save(updateEvent(updatingEvent, updateEventRequest, category));
        return eventMapper.toEventFullDto(updatedEvent);
    }

    public List<ParticipationRequestDto> updateUserEventRequestsById(Long userId, Long eventId) {
        validateAndGetEvent(eventId);
        return requestsRepository.findByEventId(eventId)
                .stream()
                .map(requestsMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult updateUserEventRequestById(Long userId, Long eventId,
                                                                     EventRequestStatusUpdateRequest updateRequest) {
        Event event = validateAndGetEvent(eventId); //checking event availability

        List<Request> requests = requestsRepository
                .findByIdInAndEventId(updateRequest.getRequestIds(), eventId);

        checkRequestStatus(requests);

        int participants = requestsRepository.countByEventIdAndStatus(eventId, "CONFIRMED");
        checkParticipantsLimit(event.getParticipantLimit(), participants);
        int freeSlots = event.getParticipantLimit() - participants;

        if (freeSlots >= requests.size()) {
            List<ParticipationRequestDto> approvedRequest = requestsRepository.saveAll(
                            setStatusToRequests(updateRequest.getStatus(), requests))
                    .stream()
                    .map(requestsMapper::toRequestDto)
                    .toList();
            EventRequestStatusUpdateResult response = EventRequestStatusUpdateResult.builder().build();
            if (updateRequest.getStatus().equals("REJECTED")) {
                response.setRejectedRequests(approvedRequest);
                response.setConfirmedRequests(List.of());
            } else {
                response.setRejectedRequests(List.of());
                response.setConfirmedRequests(approvedRequest);
            }
            return response;
        }

        List<Request> requestsToCancel = setStatusToRequests("REJECTED", requests.subList(freeSlots, requests.size()));
        requestsRepository.saveAll(requestsToCancel);

        List<ParticipationRequestDto> confirmed = requestsRepository.saveAll(
                        setStatusToRequests(updateRequest.getStatus(), requests.subList(0, freeSlots)))
                .stream()
                .map(requestsMapper::toRequestDto)
                .toList();

        List<ParticipationRequestDto> rejected = requestsRepository.saveAll(setStatusToRequests("REJECTED",
                        requests.subList(freeSlots, requests.size())))
                .stream()
                .map(requestsMapper::toRequestDto)
                .toList();

        return EventRequestStatusUpdateResult
                .builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();

    }

    public List<EventShortDto> getAllEvents(String text, List<Long> categories, String paid,
                                            String rangeStart, String rangeEnd, boolean onlyAvailable,
                                            String sort, Integer from, Integer size, HttpServletRequest request) {
        LocalDateTime start;
        LocalDateTime end;
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size);

        if (categories == null) {
            categories = List.of();
        }
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, DATE_FORMATTER);
        } else {
            start = MIN_DATETIME;
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, DATE_FORMATTER);
        } else {
            end = MAX_DATETIME;
        }

        EndpointHitDto hit = new EndpointHitDto();
        hit.setApp("main-service");
        hit.setIp(request.getRemoteAddr());
        hit.setUri(request.getRequestURI());
        hit.setTimestamp(LocalDateTime.now());
        statisticClient.saveHit(hit);

        validateDates(start, end);
        List<EventShortDto> events;
        if (paid == null) {
            events = repository
                    .searchEventsNotLookingOnPaid(text, categories, start, end, onlyAvailable, pageable)
                    .stream()
                    .map(eventMapper::toEventShortDto)
                    .toList();
        } else {
            events = repository
                    .searchEvents(text, categories, Boolean.parseBoolean(paid), start, end, onlyAvailable, pageable)
                    .stream()
                    .map(eventMapper::toEventShortDto)
                    .toList();
        }

        List<Long> eventsIds = events.stream()
                .map(EventShortDto::getId)
                .toList();

        Map<Long, Long> confirmedRequestsByEvents = requestsRepository
                .countByEventIdInAndStatusGroupByEvent(eventsIds, "CONFIRMED")
                .stream()
                .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));

        List<Long> views = ConnectToStatServer.getViews(MIN_DATETIME,
                MAX_DATETIME, ConnectToStatServer.prepareUris(eventsIds),
                true, statisticClient);

        for (int i = 0; i < events.size(); i++) {
            if ((!views.isEmpty()) && (views.get(i) != 0)) {
                events.get(i).setViews(views.get(i));
            } else {
                events.get(i).setViews(0L);
            }
            events.get(i)
                    .setConfirmedRequests(confirmedRequestsByEvents
                            .getOrDefault(events.get(i).getId(), 0L));
        }
        return events;

    }

    public EventFullDto getEventsById(Long eventId, HttpServletRequest request) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие по ID: " + eventId + " не найдено."));
        if (!event.getState().equals("PUBLISHED")) {
            throw new NotFoundException("Событие по ID: " + eventId + " не найдено.");
        }
        EndpointHitDto hit = new EndpointHitDto();
        hit.setApp("main-service");
        hit.setIp(request.getRemoteAddr());
        hit.setUri(request.getRequestURI());
        hit.setTimestamp(LocalDateTime.now());
        statisticClient.saveHit(hit);

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        List<Long> views = ConnectToStatServer.getViews(MIN_DATETIME,
                MAX_DATETIME, request.getRequestURI(), true, statisticClient);
        if (views.isEmpty()) {
            eventFullDto.setViews(0L);
            return eventFullDto;
        }
        eventFullDto.setViews(views.getFirst());
        return eventFullDto;
    }

    private Event validateAndGetEvent(long eventId) {
        Optional<Event> event = repository.findById(eventId);
        if (event.isEmpty()) {
            throw new NotFoundException("Event with id = " + eventId + " was not found");
        }
        return event.get();
    }

    private Category validateAndGetCategory(Long categoryId) {
        Optional<Category> category = categoriesRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new NotFoundException("Category with id = " + categoryId + " was not found");
        }
        return category.get();
    }

    private User validateAndGetUser(Long userId) {
        Optional<User> user = usersRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User with id = " + userId + " was not found");
        }

        return user.get();
    }

    private Event updateEvent(Event event, UpdateEventRequest eventUpdate, Category category) {

        if (eventUpdate.getAnnotation() != null) {
            event.setAnnotation(eventUpdate.getAnnotation());
        }

        if (eventUpdate.getCategory() != null) {
            event.setCategory(category);
        }

        if (eventUpdate.getDescription() != null) {
            event.setDescription(eventUpdate.getDescription());
        }

        if (eventUpdate.getLocation() != null) {
            event.setLocation(eventUpdate.getLocation());
        }

        if (eventUpdate.getPaid() != null) {
            event.setPaid(eventUpdate.getPaid());
        }

        if (eventUpdate.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdate.getParticipantLimit());
        }

        if (eventUpdate.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdate.getRequestModeration());
        }

        if (eventUpdate.getTitle() != null) {
            event.setTitle(eventUpdate.getTitle());
        }

        if (eventUpdate.getStateAction() != null) {
            if (eventUpdate.getStateAction().equals("PUBLISH_EVENT")) {
                event.setState("PUBLISHED");
                event.setPublishedOn(LocalDateTime.now());
            }

            if ((eventUpdate.getStateAction().equals("REJECT_EVENT"))
                    || (eventUpdate.getStateAction().equals("CANCEL_REVIEW"))) {
                event.setState("CANCELED");
            }

            if (eventUpdate.getStateAction().equals("SEND_TO_REVIEW")) {
                event.setState("PENDING");
            }

        }
        return event;
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Event must be published");
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Event data " + eventDate + " is before then now + 2 hours");
        }
    }

    private void checkAbilityToUpdate(Event event) {
        if (event.getState().equals("PUBLISHED")) {
            throw new ConflictException("States must be PENDING or CANCELED");
        }
    }

    private void checkRequestStatus(List<Request> request) {
        int leftIdx = 0;
        int rightIdx = request.size() - 1;
        while (leftIdx <= rightIdx) {

            if (!request.get(leftIdx).getStatus().equals("PENDING")) {
                throw new ConflictException("Request with id = " + request.get(leftIdx).getId() + " has status: "
                        + request.get(leftIdx).getStatus());
            }

            if (!request.get(rightIdx).getStatus().equals("PENDING")) {
                throw new ConflictException("Request with id = " + request.get(rightIdx).getId() + " has status: "
                        + request.get(rightIdx).getStatus());
            }
            leftIdx++;
            rightIdx--;
        }
    }

    private void checkParticipantsLimit(long participantsLimit, long participants) {
        if (participantsLimit < (participants + 1)) {
            throw new ConflictException("Exceeded requesters amount");
        }
    }

    private List<Request> setStatusToRequests(String status, List<Request> requests) {
        for (Request requestToApprove : requests) {
            requestToApprove.setStatus(status);
        }
        return requests;
    }
}
