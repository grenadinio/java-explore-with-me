package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.model.request.EventIdByRequestsCount;
import ru.practicum.mainservice.model.request.Request;

import java.util.Collection;
import java.util.List;

@Repository
public interface RequestsRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(long requesterId);

    Collection<Request> findByEventId(long eventId);

    List<Request> findByIdInAndEventId(List<Long> id, long eventId);

    Integer countByEventIdAndStatus(long eventId, String requestState);

    @Query(value = "select count(id), event " +
            "from requests " +
            "where event IN ?1 " +
            "AND status LIKE ?2 " +
            "group by event ", nativeQuery = true)
    List<EventIdByRequestsCount> countByEventIdInAndStatusGroupByEvent(List<Long> eventIds, String requestState);
}
