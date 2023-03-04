package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.EventSort;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Validated
@Slf4j
@RequiredArgsConstructor
public class EventPublicController {
    private final EventPublicService eventPublicService;

    /*
    Получение событий с возможностью фильтрации
    - если в запросе не указан диапазон дат [rangeStart-rangeEnd],
    то нужно выгружать события, которые произойдут позже текущей даты и времени
    - текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
    - в выдаче должны быть только опубликованные события
    - вариант сортировки: по дате события и по количеству просмотров.
    - информация о каждом событии должна включать в себя количество уже одобренных заявок на участие
    - только события у которых не исчерпан лимит запросов на участие
    - информация о каждом событии должна включать в себя количество просмотров
    - информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     */
    @GetMapping
    public List<EventShortDto> getSomeEventsByPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) EventSort sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request
    ) {
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        return eventPublicService.getSomeEventsByPublic(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    /*
    Получение подробной информации об опубликованном событии по его идентификатору
    - событие должно быть опубликовано
    - информация о событии должна включать в себя количество подтвержденных запросов
    - информация о событии должна включать в себя количество просмотров
    - информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     */
    @GetMapping("/{id}")
    public EventFullDto getEventByPublic(@PathVariable @NotNull @Positive Long id, HttpServletRequest request) {
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        return eventPublicService.getEventByPublic(id, request);
    }

}
