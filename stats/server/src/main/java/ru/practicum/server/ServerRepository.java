package ru.practicum.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ServerRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = """
            SELECT e.app, e.uri, COUNT(e.ip) AS hits
            FROM events e
            WHERE e.timestamp BETWEEN cast(:start AS timestamp) AND cast(:end AS timestamp)
            AND (:uris is null or e.uri IN (:uris))
            GROUP BY e.uri, e.app
            ORDER BY hits DESC""", nativeQuery = true)
    List<ViewStats> findByParams(@Param("start") Timestamp start,
                                 @Param("end") Timestamp end,
                                 @Param("uris") List<String> uris);

    @Query(value = """
            SELECT e.app, e.uri, COUNT(distinct e.ip) AS hits
            FROM events e
            WHERE e.timestamp BETWEEN cast(:start AS timestamp) AND cast(:end AS timestamp)
            AND (:uris is null or e.uri IN (:uris))
            GROUP BY e.uri, e.app
            ORDER BY hits DESC""", nativeQuery = true)
    List<ViewStats> findByParamsUniqueIp(@Param("start") Timestamp start,
                                         @Param("end") Timestamp end,
                                         @Param("uris") List<String> uris);
}
