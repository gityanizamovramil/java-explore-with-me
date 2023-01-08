package ru.practicum.ewm.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.ParticipationRequestPrivateService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@Validated
public class RequestPrivateController {

    @Autowired
    private final ParticipationRequestPrivateService participationRequestPrivateService;

    public RequestPrivateController(ParticipationRequestPrivateService participationRequestPrivateService) {
        this.participationRequestPrivateService = participationRequestPrivateService;
    }

    /*
    Получение информации о заявках текущего пользователя на участие в чужих событиях
     */
    @GetMapping
    public List<ParticipationRequestDto> getSomeParticipationRequestsByUser(
            @PathVariable @NotNull @Positive Long userId
    ) {
        return participationRequestPrivateService.getSomeParticipationRequestsByRequester(userId);
    }

    /*
    Добавление запроса от текущего пользователя на участие в событии
     */
    @PostMapping
    public ParticipationRequestDto requestParticipationByUser(
            @PathVariable @NotNull @Positive Long userId,
            @RequestParam @NotNull @Positive Long eventId
    ) {
        return participationRequestPrivateService.requestParticipationByUser(userId, eventId);
    }

    /*
    Отмена своего запроса на участие в событии
     */
    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationByUser(
            @PathVariable @NotNull @Positive Long userId,
            @PathVariable @NotNull @Positive Long requestId
    ) {
        return participationRequestPrivateService.cancelParticipationByRequester(userId, requestId);
    }
}
