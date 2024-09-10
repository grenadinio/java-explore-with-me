package ru.practicum.mainservice.model.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.model.category.CategoriesMapper;
import ru.practicum.mainservice.model.category.Category;
import ru.practicum.mainservice.model.user.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final CategoriesMapper categoriesMapper;
    private final UserMapper userMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                categoriesMapper.toCategoryDto(event.getCategory()),
                null,
                event.getEventDate().format(DATE_FORMATTER),
                userMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                null
        );
    }

    public EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                categoriesMapper.toCategoryDto(event.getCategory()),
                null,
                event.getCreatedOn().format(DATE_FORMATTER),
                event.getDescription(),
                event.getEventDate().format(DATE_FORMATTER),
                userMapper.toUserShortDto(event.getInitiator()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn() == null ? null : event.getPublishedOn().toString(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                null
        );
    }

    public Event toEvent(NewEventDto newEventDto, Category category) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        event.setLocation(newEventDto.getLocation());
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());
        return event;
    }
}
