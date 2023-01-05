package ru.practicum.ewm.event.service;

import org.springframework.lang.Nullable;
import ru.practicum.ewm.common.EventSort;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventPublicService {

    EventFullDto getEventByPublic(Long eventId, HttpServletRequest request);

    List<EventShortDto> getSomeEventsByPublic(
            @Nullable String text,
            @Nullable List<Long> categories,
            @Nullable Boolean paid,
            @Nullable LocalDateTime rangeStart,
            @Nullable LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            @Nullable EventSort sort,
            Integer from,
            Integer size,
            HttpServletRequest request
    );
}
