package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventRequest;

import java.util.List;

public interface EventPrivateService {

    List<EventShortDto> getSomeEventsByUser(Long userId, Integer from, Integer size);

    EventFullDto postEventByUser(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUser(Long userId, Long eventId);

    EventFullDto patchEventByUser(Long userId, UpdateEventRequest updateEventRequest);

    EventFullDto cancelEventByUser(Long userId, Long eventId);

}
