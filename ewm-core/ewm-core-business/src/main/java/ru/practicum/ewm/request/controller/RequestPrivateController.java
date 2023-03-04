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
    Добавление запроса от текущего пользователя на участие в событии
    - нельзя добавить повторный запрос
    - инициатор события не может добавить запрос на участие в своём событии
    - нельзя участвовать в неопубликованном событии
    - если у события достигнут лимит запросов на участие - необходимо вернуть ошибку
    - если для события отключена пре-модерация запросов на участие,
    то запрос должен автоматически перейти в состояние подтвержденного
    - если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
    - нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие
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
