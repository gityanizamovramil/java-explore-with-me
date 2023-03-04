package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.EventState;
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
@RequiredArgsConstructor
public class EventAdminController {
    private final EventAdminService eventAdminService;

    /*
    Поиск событий (возвращает полную информацию обо всех событиях подходящих под переданные условия)
     */
    @GetMapping
    public List<EventFullDto> getEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return eventAdminService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    /*
    Редактирование любого события администратором. Валидация данных не требуется.
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
    - дата начала события должна быть не ранее чем за час от даты публикации
    - событие должно быть в состоянии ожидания публикации
     */
    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEventByAdmin(@PathVariable @NotNull @Positive Long eventId) {
        return eventAdminService.publishEventByAdmin(eventId);
    }

    /*
    Отклонение события (отклоняемое событие не должно быть опубликованным ранее)
     */
    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEventByAdmin(@PathVariable @NotNull @Positive Long eventId) {
        return eventAdminService.rejectEventByAdmin(eventId);
    }
}
