package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class RequestPrivateController {
    private final ParticipationRequestPrivateService participationRequestPrivateService;

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
    Добавление запроса от текущего пользователя на участие в опубликованном событии с незакрытым лимитом участия
    - повторные запросы и запрос от инициатора не подтверждаются
    - при достижении лимита по заявкам на данное событие, запрос не подтверждается
    - запрос на событие, имеющее отключенную пре-модерацию или 0-й лимит заявок, автоматически подтверждается
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
