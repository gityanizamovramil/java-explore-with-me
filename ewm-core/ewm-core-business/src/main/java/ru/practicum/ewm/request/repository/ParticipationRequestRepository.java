package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.common.RequestStatus;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.model.RequestCount;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    Boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    @Query("SELECT new ru.practicum.ewm.request.model.RequestCount(" +
            "r.event.id, COUNT(r.event.id), r.status) " +
            "FROM ParticipationRequest AS r " +
            "WHERE ((:eventIds) IS NULL OR r.event.id IN (:eventIds)) " +
            "AND (r.status = :status) " +
            "GROUP BY r.id")
    List<RequestCount> fetchRequestCountsByEventIdAndStatus(List<Long> eventIds, RequestStatus status);
}
