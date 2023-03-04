package ru.practicum.ewm.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.service.EndpointHitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
public class StatsController {
    private final EndpointHitService endpointHitService;

    /*
    Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
    - название сервиса, uri и ip пользователя указаны в теле запроса
     */
    @PostMapping("/hit")
    @Transactional
    public EndpointHitDto postEndpointHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        return endpointHitService.createHit(endpointHitDto);
    }

    /*
    Получение статистики по посещениям.
     */
    @GetMapping("/stats")
    @Transactional
    public List<ViewStatsDto> getViewStats(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique
    ) {
        List<ViewStatsDto> viewStats = endpointHitService.getViewStats(start, end, uris, unique);
        log.info("\n \nStarting logging views stats:\nviews: {}\nstart: {}\nend: {}\nuris: {}\n ",
                viewStats, start, end, uris);
        return viewStats;
    }
}
