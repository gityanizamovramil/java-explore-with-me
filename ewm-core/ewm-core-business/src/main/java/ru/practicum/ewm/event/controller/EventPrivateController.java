package ru.practicum.ewm.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventRequest;
import ru.practicum.ewm.event.service.EventPrivateService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.ParticipationRequestPrivateService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@Validated
public class EventPrivateController {

    @Autowired
    private final EventPrivateService eventPrivateService;

    @Autowired
    private final ParticipationRequestPrivateService participationRequestPrivateService;

    public EventPrivateController(EventPrivateService eventPrivateService,
                                  ParticipationRequestPrivateService participationRequestPrivateService) {
        this.eventPrivateService = eventPrivateService;
        this.participationRequestPrivateService = participationRequestPrivateService;
    }

    /*
    Получение событий, добавленных текущим пользователем
     */
    @GetMapping
    public List<EventShortDto> getSomeEventsByUser(
            @PathVariable @NotNull @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return eventPrivateService.getSomeEventsByUser(userId, from, size);
    }

    /*
    Изменение события добавленного текущим пользователем
     */
    @PatchMapping
    public EventFullDto patchEventByUser(
            @PathVariable @NotNull @Positive Long userId,
            @RequestBody @Valid UpdateEventRequest updateEventRequest
    ) {
        return eventPrivateService.patchEventByUser(userId, updateEventRequest);
    }

    /*
    Добавление нового события
     */
    @PostMapping
    public EventFullDto postEventByUser(
            @PathVariable @NotNull @Positive Long userId,
            @RequestBody @Valid NewEventDto newEventDto
    ) {
        return eventPrivateService.postEventByUser(userId, newEventDto);
    }

    /*
    Получение полной информации о событии добавленном текущим пользователем
     */
    @GetMapping("/{eventId}")
    public EventFullDto getEventByUser(
            @PathVariable @NotNull @Positive Long userId,
            @PathVariable @NotNull @Positive Long eventId
    ) {
        return eventPrivateService.getEventByUser(userId, eventId);
    }

    /*
    Отмена события добавленного текущим пользователем.
     */
    @PatchMapping("/{eventId}")
    public EventFullDto cancelEventByUser(
            @PathVariable @NotNull @Positive Long userId,
            @PathVariable @NotNull @Positive Long eventId
    ) {
        return eventPrivateService.cancelEventByUser(userId, eventId);
    }

    /*
    Получение информации о запросах на участие в событии текущего пользователя
     */
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsByUser(
            @PathVariable @NotNull @Positive Long userId,
            @PathVariable @NotNull @Positive Long eventId
    ) {
        return participationRequestPrivateService.getParticipationRequestsByInitiator(userId, eventId);
    }

    /*
    Подтверждение чужой заявки на участие в событии текущего пользователя
     */
    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmParticipationRequestByUser(
            @PathVariable @NotNull @Positive Long userId,
            @PathVariable @NotNull @Positive Long eventId,
            @PathVariable @NotNull @Positive Long reqId
    ) {
        return participationRequestPrivateService.confirmParticipationRequestByInitiator(userId, eventId, reqId);
    }

    /*
    Отклонение чужой заявки на участие в событии текущего пользователя
     */
    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectParticipationRequestByUser(
            @PathVariable @NotNull @Positive Long userId,
            @PathVariable @NotNull @Positive Long eventId,
            @PathVariable @NotNull @Positive Long reqId
    ) {
        return participationRequestPrivateService.rejectParticipationRequestByInitiator(userId, eventId, reqId);
    }

}
