package ru.practicum.ewm.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.EventState;
import ru.practicum.ewm.common.RequestStatus;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.request.service.ParticipationRequestPrivateService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationRequestService implements ParticipationRequestPrivateService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByInitiator(Long userId, Long eventId) {
        List<ParticipationRequest> requests = participationRequestRepository.findAllByEventId(eventId);
        requests.forEach(r -> validateInitiatorId(r.getEvent().getInitiator(), userId));
        return ParticipationRequestMapper.toParticipationRequestDtoList(requests);
    }

    @Override
    public List<ParticipationRequestDto> getSomeParticipationRequestsByRequester(Long userId) {
        List<ParticipationRequest> requests = participationRequestRepository.findAllByRequesterId(userId);
        return ParticipationRequestMapper.toParticipationRequestDtoList(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto confirmParticipationRequestByInitiator(Long userId, Long eventId, Long reqId) {
        ParticipationRequest participationRequest = findById(reqId);
        Event event = participationRequest.getEvent();
        User initiator = event.getInitiator();
        validateEventId(event, eventId);
        validateInitiatorId(initiator, userId);
        validateConfirms(event);
        participationRequest.setStatus(RequestStatus.CONFIRMED);
        participationRequest = participationRequestRepository.save(participationRequest);
        rejectPendingParticipationRequests(event);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto rejectParticipationRequestByInitiator(Long userId, Long eventId, Long reqId) {
        ParticipationRequest participationRequest = findById(reqId);
        Event event = participationRequest.getEvent();
        User initiator = event.getInitiator();
        validateEventId(event, eventId);
        validateInitiatorId(initiator, userId);
        participationRequest.setStatus(RequestStatus.REJECTED);
        participationRequest = participationRequestRepository.save(participationRequest);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationByRequester(Long userId, Long requestId) {
        ParticipationRequest participationRequest = findById(requestId);
        validateRequester(participationRequest.getRequester(), userId);
        validateCancel(participationRequest);
        participationRequest.setStatus(RequestStatus.CANCELED);
        participationRequest = participationRequestRepository.save(participationRequest);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto requestParticipationByUser(Long userId, Long eventId) {
        Event event = findEventById(eventId);
        User requester = findUserById(userId);
        validateRequestRepeating(event, requester);
        validateRequesterIsNotInitiator(event, requester);
        validateEventIsPublished(event);
        validateConfirms(event);
        ParticipationRequest participationRequest =
                ParticipationRequestMapper.toParticipationRequest(event, requester, RequestStatus.PENDING);
        participationRequest = participationRequestRepository.save(participationRequest);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    private void validateEventIsPublished(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Participation request is not allowed to non-published event");
        }
    }

    private void validateRequestRepeating(Event event, User requester) {
        if (participationRequestRepository.existsByRequesterIdAndEventId(requester.getId(), event.getId()))
            throw new ValidationException("Participation request for the event is already created before by user");
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(
                        () -> {
                            throw new ObjectNotFoundException("Event for participation request not found");
                        }
                );
    }

    private void validateRequesterIsNotInitiator(Event event, User requester) {
        if (event.getInitiator().getId().equals(requester.getId())) {
            throw new ValidationException("Event participation requester cannot be initiator of that event");
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> {
                            throw new ObjectNotFoundException("Event for participation request not found");
                        }
                );
    }

    private void validateCancel(ParticipationRequest participationRequest) {
        if (participationRequest.getStatus().equals(RequestStatus.CANCELED)) {
            throw new ValidationException("Participation request is already cancelled before");
        }
    }

    private void rejectPendingParticipationRequests(Event event) {
        if (event.getParticipantLimit().equals(0) || Boolean.TRUE.equals(!event.getRequestModeration())) {
            return;
        }
        Long confirms = participationRequestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        if (confirms >= event.getParticipantLimit()) {
            List<ParticipationRequest> participationRequests =
                    participationRequestRepository.findAllByEventIdAndStatus(event.getId(), RequestStatus.PENDING);
            participationRequests.forEach(p -> p.setStatus(RequestStatus.REJECTED));
            participationRequestRepository.saveAll(participationRequests);
        }
    }

    private ParticipationRequest findById(Long reqId) {
        return participationRequestRepository.findById(reqId)
                .orElseThrow(() -> {
                            throw new ObjectNotFoundException("Request not found");
                        }
                );
    }

    private void validateEventId(Event event, Long eventId) {
        if (!event.getId().equals(eventId)) {
            throw new ValidationException("Event does not match for participation request");
        }
    }

    private void validateInitiatorId(User initiator, Long userId) {
        if (!initiator.getId().equals(userId)) {
            throw new ValidationException("Initiator does not match for event of the participation request");
        }
    }

    private void validateRequester(User requester, Long userId) {
        if (!requester.getId().equals(userId)) {
            throw new ValidationException("Requester does not match for event of the participation request");
        }
    }

    private void validateConfirms(Event event) {
        if (Boolean.TRUE.equals(!event.getRequestModeration()) || event.getParticipantLimit().equals(0)) {
            return;
        }
        Long confirms = participationRequestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        if (confirms >= event.getParticipantLimit()) {
            throw new ValidationException("Event already out of participant limit");
        }
    }
}
