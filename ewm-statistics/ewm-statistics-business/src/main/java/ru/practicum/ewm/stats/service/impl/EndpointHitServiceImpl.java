package ru.practicum.ewm.stats.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.mapper.EndpointHitMapper;
import ru.practicum.ewm.stats.mapper.ViewStatsMapper;
import ru.practicum.ewm.stats.model.EndpointHit;
import ru.practicum.ewm.stats.repository.EndpointHitRepository;
import ru.practicum.ewm.stats.service.EndpointHitService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EndpointHitServiceImpl implements EndpointHitService {

    @Autowired
    private final EndpointHitRepository endpointHitRepository;

    public EndpointHitServiceImpl(EndpointHitRepository endpointHitRepository) {
        this.endpointHitRepository = endpointHitRepository;
    }

    @Override
    public EndpointHitDto createHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(endpointHitDto);
        return EndpointHitMapper.toEndpointHitDto(endpointHitRepository.save(endpointHit));
    }

    @Override
    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) ViewStatsMapper.toViewStatsDtoList(endpointHitRepository.fetchByUniqueIp(start, end, uris));
        return ViewStatsMapper.toViewStatsDtoList(endpointHitRepository.fetch(start, end, uris));
    }


}
