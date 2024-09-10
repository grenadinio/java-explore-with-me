package ru.practicum.mainservice.model.compilation;

import ru.practicum.mainservice.model.event.Event;

public interface EventByCompId {

    Long getCompilationId();

    Event getEvent();

}
