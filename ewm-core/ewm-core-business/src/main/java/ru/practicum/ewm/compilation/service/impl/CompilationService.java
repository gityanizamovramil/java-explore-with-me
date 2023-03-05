package ru.practicum.ewm.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.RequestStatus;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.compilation.service.CompilationAdminService;
import ru.practicum.ewm.compilation.service.CompilationPublicService;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.model.RequestCount;
import ru.practicum.ewm.request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.service.StatisticsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationService implements CompilationPublicService, CompilationAdminService {
    private final LocalDateTime epochStart = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    private final LocalDateTime epochEnd = LocalDateTime.of(2100, 12, 31, 23, 59, 59);
    private final String uri = "/events";
    private final CompilationRepository compilationRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatisticsService statisticsService;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getSomeCompilationsByPublic(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        compilations.stream()
                .map(Compilation::getEvents)
                .map(ArrayList::new)
                .forEach(events -> {
                    pullConfirmsToEvents(events);
                    pullStatsToEvents(events);
                });
        return CompilationMapper.toCompilationDtoList(compilations);
    }

    @Override
    public CompilationDto getCompilationByPublic(Long compId) {
        Compilation compilation = findById(compId);
        List<Event> events = new ArrayList<>(compilation.getEvents());
        pullConfirmsToEvents(events);
        pullStatsToEvents(events);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto postCompilationByAdmin(NewCompilationDto newCompilationDto) {
        List<Event> events = getEventList(newCompilationDto.getEvents());
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        compilation = compilationRepository.save(compilation);
        events = new ArrayList<>(compilation.getEvents());
        pullConfirmsToEvents(events);
        pullStatsToEvents(events);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilationByAdmin(Long compId) {
        existsById(compId);
        compilationRepository.deleteById(compId);
    }

    private void existsById(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation not found.");
        }
    }

    @Override
    @Transactional
    public void deleteEventFromCompilationByAdmin(Long compId, Long eventId) {
        Compilation compilation = findById(compId);
        Event event = findEventInCompilation(compilation, eventId);
        compilation.getEvents().remove(event);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void unpinCompilationByAdmin(Long compId) {
        Compilation compilation = findById(compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void pinCompilationByAdmin(Long compId) {
        Compilation compilation = findById(compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void addEventToCompilationByAdmin(Long compId, Long eventId) {
        Compilation compilation = findById(compId);
        Event event = findEventById(eventId);
        validateEventAdding(compilation, event);
        compilation.getEvents().add(event);
        compilationRepository.save(compilation);
    }

    private List<Event> getEventList(List<Long> eventIds) {
        return eventIds.stream()
                .map(eventId -> eventRepository.findById(eventId).orElseThrow(
                        () -> {
                            throw new NotFoundException("Event for compilation creation is not found");
                        }))
                .collect(Collectors.toList());
    }

    private void validateEventAdding(Compilation compilation, Event event) {
        if (compilation.getEvents().contains(event)) {
            throw new BadRequestException("Event added to compilation already before");
        }
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> {
                    throw new NotFoundException("Event mentioned for compilation adding not found");
                }
        );
    }

    private Event findEventInCompilation(Compilation compilation, Long eventId) {
        return compilation.getEvents().stream()
                .filter(event -> event.getId().equals(eventId))
                .findFirst().orElseThrow(
                        () -> {
                            throw new NotFoundException("Event in that compilation not found");
                        }
                );
    }

    private Compilation findById(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> {
                    throw new NotFoundException("Compilation not found.");
                }
        );
    }

    private void pullStatsToEvents(List<Event> events) {
        Map<Long, Event> eventMap = new HashMap<>();
        events.forEach(e -> eventMap.put(e.getId(), e));
        List<Long> eventIds = new ArrayList<>(eventMap.keySet());
        List<ViewStatsDto> views = statisticsService.getSomeViews(epochStart, epochEnd, eventIds, uri, false);
        views.forEach(v -> eventMap.get(v.getIdFromUri()).setViews(v.getHits()));
    }

    private void pullConfirmsToEvents(List<Event> events) {
        Map<Long, Event> eventMap = new HashMap<>();
        events.forEach(e -> eventMap.put(e.getId(), e));
        List<Long> eventIds = new ArrayList<>(eventMap.keySet());
        List<RequestCount> counts = countParticipationRequests(eventIds, RequestStatus.CONFIRMED);
        counts.forEach(c -> eventMap.get(c.getEventId()).setConfirmedRequests(c.getParticipationCount()));
    }

    private List<RequestCount> countParticipationRequests(List<Long> eventIds, RequestStatus requestStatus) {
        return participationRequestRepository.fetchRequestCountsByEventIdAndStatus(eventIds, requestStatus);
    }
}
