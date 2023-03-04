package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.stats.model.EndpointHit;
import ru.practicum.ewm.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = "SELECT new ru.practicum.ewm.stats.model.ViewStats(hits.app, hits.uri, COUNT(hits.ip)) " +
            "FROM EndpointHit as hits " +
            "WHERE ((:uris) IS NULL OR hits.uri IN (:uris)) " +
            "AND " +
            "(hits.timestamp >= :start) " +
            "AND " +
            "(hits.timestamp <= :end) " +
            "GROUP BY hits.app, hits.uri")
    List<ViewStats> fetch(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);

    @Query(value = "SELECT new ru.practicum.ewm.stats.model.ViewStats(hits.app, hits.uri, COUNT(DISTINCT hits.ip)) " +
            "FROM EndpointHit as hits " +
            "WHERE ((:uris) IS NULL OR hits.uri IN (:uris)) " +
            "AND " +
            "(hits.timestamp >= :start) " +
            "AND " +
            "(hits.timestamp <= :end) " +
            "GROUP BY hits.app, hits.uri")
    List<ViewStats> fetchByUniqueIp(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);
}
