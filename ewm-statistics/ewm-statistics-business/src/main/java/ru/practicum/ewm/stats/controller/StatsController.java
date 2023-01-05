package ru.practicum.ewm.stats.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.Pattern;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.service.EndpointHitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@Slf4j
public class StatsController {

    @Autowired
    private final EndpointHitService endpointHitService;

    public StatsController(EndpointHitService endpointHitService) {
        this.endpointHitService = endpointHitService;
    }

    /*
    Сохранение информации о том, что к эндпоинту был запрос
    Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
    Название сервиса, uri и ip пользователя указаны в теле запроса.
     */
    @PostMapping("/hit")
    @Transactional
    public EndpointHitDto postEndpointHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        return endpointHitService.createHit(endpointHitDto);
    }

    /*
    Получение статистики по посещениям.
    Обратите внимание: значение даты и времени нужно закодировать (например используя java.net.URLEncoder.encode)
     */
    @GetMapping("/stats")
    @Transactional
    public List<ViewStatsDto> getViewStats(
            @RequestParam @DateTimeFormat(pattern = Pattern.LOCAL_DATE_TIME_FORMAT) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = Pattern.LOCAL_DATE_TIME_FORMAT) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique
    ) {
        List<ViewStatsDto> viewStats= endpointHitService.getViewStats(start, end, uris, unique);
        log.info("Starting logging getted views:");
        log.info(start.toString());
        log.info(end.toString());
        log.info(uris.toString());
        log.info(viewStats.toString());
        return viewStats;
    }
}
