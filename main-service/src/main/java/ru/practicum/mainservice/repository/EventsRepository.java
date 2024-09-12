package ru.practicum.mainservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.model.event.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventsRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.category.id = :id")
    Collection<Event> findByCategoryId(Long id);

    @Query("SELECT e FROM Event e WHERE e.id IN :ids")
    Collection<Event> findAllByIds(List<Long> ids);

    List<Event> findByInitiatorId(long userId, Pageable pageable);

    Optional<Event> findByIdAndState(long id, String state);

    @Query("SELECT e FROM Event e JOIN EventsByCompilation")
    Collection<Event> findAllByCompilationId(Long compId);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE ((e.state IN :states OR :states IS NULL) " +
            "AND (e.category.id IN :category OR :category IS NULL) " +
            "AND (e.initiator.id IN :initiator OR :initiator IS NULL) " +
            "AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd)) ")
    List<Event> findByConditionals(List<String> states, List<Long> category, List<Long> initiator,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE (((e.annotation ILIKE %:text% OR e.description ILIKE %:text%) OR :text IS NULL) " +
            "AND (e.category.id IN :category OR :category IS NULL) " +
            "AND (e.paid = CAST(:paid AS boolean) OR :paid IS NULL) " +
            "AND (e.eventDate BETWEEN :rangStart AND :rangeEnd ) " +
            "AND (CAST(:isAvailable AS BOOLEAN) is TRUE " +
            "  OR( " +
            "  select count(id) " +
            "  from Request AS r " +
            "  WHERE r.event.id = e.id) < e.participantLimit) " +
            "AND state = 'PUBLISHED') ")
    List<Event> searchEvents(String text, List<Long> category, boolean paid, LocalDateTime rangStart,
                             LocalDateTime rangeEnd, boolean isAvailable, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE (((e.annotation ILIKE %:text% OR e.description ILIKE %:text%) OR :text IS NULL) " +
            "AND (e.category.id IN :category OR :category IS NULL) " +
            "AND (e.eventDate BETWEEN :rangStart AND :rangeEnd ) " +
            "AND (CAST(:isAvailable AS BOOLEAN) is TRUE " +
            "  OR( " +
            "  select count(id) " +
            "  from Request AS r " +
            "  WHERE r.event.id = e.id) < e.participantLimit) " +
            "AND state = 'PUBLISHED') ")
    List<Event> searchEventsNotLookingOnPaid(String text, List<Long> category, LocalDateTime rangStart,
                                             LocalDateTime rangeEnd, boolean isAvailable, Pageable pageable);
}
