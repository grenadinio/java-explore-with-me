package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.compilation.Compilation;
import ru.practicum.mainservice.model.compilation.CompilationDto;
import ru.practicum.mainservice.model.compilation.CompilationsMapper;
import ru.practicum.mainservice.model.compilation.CompositeKeyForEventByComp;
import ru.practicum.mainservice.model.compilation.EventByCompId;
import ru.practicum.mainservice.model.compilation.EventsByCompilation;
import ru.practicum.mainservice.model.compilation.NewCompilationDto;
import ru.practicum.mainservice.model.compilation.UpdateCompilationRequest;
import ru.practicum.mainservice.model.event.Event;
import ru.practicum.mainservice.model.event.EventMapper;
import ru.practicum.mainservice.model.event.EventShortDto;
import ru.practicum.mainservice.repository.CompilationsRepository;
import ru.practicum.mainservice.repository.EventByCompilationRepository;
import ru.practicum.mainservice.repository.EventsRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationsService {
    private final CompilationsRepository repository;
    private final EventByCompilationRepository eventByCompilationRepository;
    private final EventsRepository eventsRepository;
    private final CompilationsMapper mapper;
    private final EventMapper eventMapper;

    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = repository.save(mapper.toCompilation(newCompilationDto));

        List<EventShortDto> events = addEventsToCompilation(compilation.getId(), newCompilationDto.getEvents());

        return mapper.toCompilationDto(compilation, events);
    }

    public void deleteCompilation(Long compId) {
        validateAndGetCompilation(compId);
        repository.deleteById(compId);
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = validateAndGetCompilation(compId);
        List<EventShortDto> events;

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null) {
            events = addEventsToCompilation(compilation.getId(),
                    updateCompilationRequest.getEvents());
        } else {
            events = eventByCompilationRepository.findEventsByCompilationId(compId).stream()
                    .map(eventMapper::toEventShortDto)
                    .toList();
        }

        return mapper.toCompilationDto(repository.save(compilation), events);
    }

    @Transactional(readOnly = true)
    public List<CompilationDto> getAllCompilations(boolean pinned, Integer from, Integer size) {
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size);

        Map<Long, Compilation> compilationMap = repository.findAll(pageable)
                .stream()
                .collect(Collectors.toMap(Compilation::getId, Function.identity()));

        List<EventByCompId> eventsByCompId = eventByCompilationRepository
                .findEventsByCompilationIds(compilationMap.keySet());

        Map<Long, List<EventShortDto>> eventShortListByCompId = new HashMap<>();

        for (EventByCompId eventByCompId : eventsByCompId) {
            if (!eventShortListByCompId.containsKey(eventByCompId.getCompilationId())) {
                Event event = eventByCompId.getEvent();
                List<EventShortDto> events;
                if (event != null) {
                    events = new ArrayList<>();
                    events.add(eventMapper.toEventShortDto(eventByCompId.getEvent()));
                } else {
                    events = List.of();
                }
                eventShortListByCompId.put(eventByCompId.getCompilationId(), events);
                continue;
            }
            if (eventByCompId.getEvent() == null) {
                continue;
            }
            eventShortListByCompId.get(eventByCompId.getCompilationId())
                    .add(eventMapper.toEventShortDto(eventByCompId.getEvent()));
        }

        List<CompilationDto> compilationResponses = new ArrayList<>();

        for (Compilation compilation : compilationMap.values()) {
            List<EventShortDto> events = eventShortListByCompId.get(compilation.getId());
            if (events == null) {
                events = List.of();
            }
            compilationResponses.add(mapper.toCompilationDto(compilation, events));
        }
        return compilationResponses;
    }

    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = validateAndGetCompilation(compId);
        List<EventShortDto> events = eventByCompilationRepository.findEventsByCompilationId(compId).stream()
                .map(eventMapper::toEventShortDto)
                .toList();
        return mapper.toCompilationDto(compilation, events);
    }

    private Compilation validateAndGetCompilation(Long compId) {
        Optional<Compilation> compilation = repository.findById(compId);

        if (compilation.isEmpty()) {
            throw new NotFoundException("Compilation with id = " + compId + " was not found");
        }
        return compilation.get();
    }

    private List<EventShortDto> addEventsToCompilation(Long compId, List<Long> eventsIds) {
        List<EventsByCompilation> eventsByComp;
        if (eventsIds != null) {
            eventsByComp = eventsIds.stream()
                    .map((eventId) -> new EventsByCompilation(new CompositeKeyForEventByComp(compId, eventId)))
                    .toList();
        } else {
            eventsByComp = new ArrayList<>();
        }

        eventByCompilationRepository.saveAll(eventsByComp);

        return eventsRepository.findAllByIds(eventsIds).stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }
}
