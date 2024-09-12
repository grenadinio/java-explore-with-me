package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.model.compilation.CompositeKeyForEventByComp;
import ru.practicum.mainservice.model.compilation.EventByCompId;
import ru.practicum.mainservice.model.compilation.EventsByCompilation;
import ru.practicum.mainservice.model.event.Event;

import java.util.Collection;
import java.util.List;

@Repository
public interface EventByCompilationRepository extends JpaRepository<EventsByCompilation, CompositeKeyForEventByComp> {
    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.id IN " +
            "(SELECT ec.compositeKey.eventId " +
            "FROM EventsByCompilation AS ec " +
            "WHERE ec.compositeKey.compilationId = :compilationId)")
    List<Event> findEventsByCompilationId(Long compilationId);

    @Query(value = "select compilation_id, e.* " +
            "from events_by_compilations AS ebc " +
            "INNER JOIN events AS e on ebc.event_id = e.id " +
            "where compilation_id IN (?1) ",
            nativeQuery = true)
    List<EventByCompId> findEventsByCompilationIds(Collection<Long> compilationIds);
}
