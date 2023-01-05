package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.common.RequestStatus;
import ru.practicum.ewm.request.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Long countByEvent_IdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEvent_Id(Long eventId);

    List<ParticipationRequest> findAllByRequester_Id(Long requesterId);

    List<ParticipationRequest> findAllByRequester_IdAndEvent_Id(Long requesterId, Long eventId);

    List<ParticipationRequest> findAllByEvent_IdAndStatus(Long eventId, RequestStatus status);

}
