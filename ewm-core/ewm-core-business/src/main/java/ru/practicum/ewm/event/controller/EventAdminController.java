package ru.practicum.ewm.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.EventState;
import ru.practicum.ewm.common.Pattern;
import ru.practicum.ewm.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.service.EventAdminService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Validated
public class EventAdminController {

    @Autowired
    private final EventAdminService eventAdminService;

    public EventAdminController(EventAdminService eventAdminService) {
        this.eventAdminService = eventAdminService;
    }

    /*
    Поиск событий
     */
    @GetMapping
    public List<EventFullDto> getEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = Pattern.LOCAL_DATE_TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = Pattern.LOCAL_DATE_TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return eventAdminService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    /*
    Редактирование события
     */
    @PutMapping("/{eventId}")
    public EventFullDto putEventByAdmin(
            @PathVariable @NotNull @Positive Long eventId,
            @RequestBody AdminUpdateEventRequest adminUpdateEventRequest
    ) {
        return eventAdminService.putEventByAdmin(eventId, adminUpdateEventRequest);
    }

    /*
    Публикация события
     */
    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEventByAdmin(
            @PathVariable @NotNull @Positive Long eventId
    ) {
        return eventAdminService.publishEventByAdmin(eventId);
    }

    /*
    Отклонение события
     */
    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEventByAdmin(
            @PathVariable @NotNull @Positive Long eventId
    ) {
        return eventAdminService.rejectEventByAdmin(eventId);
    }
}
