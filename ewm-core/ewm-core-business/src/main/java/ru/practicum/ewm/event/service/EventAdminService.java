package ru.practicum.ewm.event.service;

import ru.practicum.ewm.common.EventState;
import ru.practicum.ewm.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.event.dto.EventFullDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminService {

    List<EventFullDto> getEventsByAdmin(List<Long> users,
                                        List<EventState> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size
    );

    EventFullDto publishEventByAdmin(Long eventId);

    EventFullDto rejectEventByAdmin(Long eventId);

    EventFullDto putEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest);
}
