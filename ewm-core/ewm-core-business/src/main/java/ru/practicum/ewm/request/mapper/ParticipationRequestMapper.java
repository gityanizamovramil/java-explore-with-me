package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.common.RequestStatus;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public final class ParticipationRequestMapper {
    private ParticipationRequestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static List<ParticipationRequestDto> toParticipationRequestDtoList(
            List<ParticipationRequest> participationRequests
    ) {
        return participationRequests.stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent().getId())
                .requester(participationRequest.getRequester().getId())
                .status(participationRequest.getStatus())
                .build();
    }

    public static ParticipationRequest toParticipationRequest(Event event, User requester, RequestStatus status) {
        return ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(status)
                .build();
    }
}
