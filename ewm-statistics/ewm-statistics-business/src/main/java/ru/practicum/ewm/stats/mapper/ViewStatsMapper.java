package ru.practicum.ewm.stats.mapper;

import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.model.ViewStats;

import java.util.List;
import java.util.stream.Collectors;

public final class ViewStatsMapper {
    private ViewStatsMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }

    public static List<ViewStatsDto> toViewStatsDtoList(List<ViewStats> viewStatsList) {
        return viewStatsList.stream()
                .map(ViewStatsMapper::toViewStatsDto)
                .collect(Collectors.toList());
    }
}
