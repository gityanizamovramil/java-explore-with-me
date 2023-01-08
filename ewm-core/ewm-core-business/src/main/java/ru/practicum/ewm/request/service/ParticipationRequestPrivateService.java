package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestPrivateService {

    List<ParticipationRequestDto> getParticipationRequestsByInitiator(Long userId, Long eventId);

    List<ParticipationRequestDto> getSomeParticipationRequestsByRequester(Long userId);

    ParticipationRequestDto confirmParticipationRequestByInitiator(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto rejectParticipationRequestByInitiator(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto cancelParticipationByRequester(Long userId, Long requestId);

    ParticipationRequestDto requestParticipationByUser(Long userId, Long eventId);
}
