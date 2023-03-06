package ru.practicum.ewm.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.common.EventSort;
import ru.practicum.ewm.common.EventState;
import ru.practicum.ewm.common.LocationDto;
import ru.practicum.ewm.common.RequestStatus;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.EventAdminService;
import ru.practicum.ewm.event.service.EventPrivateService;
import ru.practicum.ewm.event.service.EventPublicService;
import ru.practicum.ewm.exception.AccessException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.location.mapper.LocationMapper;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.request.model.RequestCount;
import ru.practicum.ewm.request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.service.StatisticsService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventPublicService, EventPrivateService, EventAdminService {
    private final LocalDateTime epochStart = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    private final LocalDateTime epochEnd = LocalDateTime.of(2100, 12, 31, 23, 59, 59);
    private final String uri = "/events";
    private final EventRepository eventRepository;
    private final StatisticsService statisticsService;
    private final ParticipationRequestRepository participationRequestRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    @Override
    public EventFullDto getEventByPublic(Long eventId, HttpServletRequest request) {
        Event event = findById(eventId);
        validatePublicAccess(event);
        pullConfirmsToEvents(List.of(event));
        pullStatsToEvents(List.of(event));
        statisticsService.makeView(request);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getSomeEventsByPublic(
            @Nullable String text,
            @Nullable List<Long> categories,
            @Nullable Boolean paid,
            @Nullable LocalDateTime rangeStart,
            @Nullable LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            @Nullable EventSort eventSort,
            Integer from,
            Integer size,
            HttpServletRequest request) {
        rangeStart = rangeStart == null ? LocalDateTime.now() : rangeStart;
        rangeEnd = rangeEnd == null ? epochEnd : rangeEnd;
        List<Event> events = findSortedByEventDate(
                null, text, categories, paid, rangeStart, rangeEnd, from, size, List.of(EventState.PUBLISHED));
        pullConfirmsToEvents(events);
        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = filterOnlyAvailable(events);
        }
        pullStatsToEvents(events);
        if (eventSort != null && eventSort.equals(EventSort.VIEWS)) {
            events = sortByViews(events);
        }
        statisticsService.makeView(request);
        return EventMapper.toEventShortDtoList(events);
    }

    private List<Event> sortByViews(List<Event> events) {
        return events.stream().sorted(Comparator.comparing(Event::getViews)).collect(Collectors.toList());
    }

    private void pullConfirmsToEvents(List<Event> events) {
        Map<Long, Event> eventMap = new HashMap<>();
        events.forEach(e -> eventMap.put(e.getId(), e));
        List<Long> eventIds = new ArrayList<>(eventMap.keySet());
        List<RequestCount> counts = countParticipationRequests(eventIds, RequestStatus.CONFIRMED);
        counts.forEach(c -> eventMap.get(c.getEventId()).setConfirmedRequests(
                c.getParticipationCount() == null ? 0L : c.getParticipationCount()));
    }

    private void pullStatsToEvents(List<Event> events) {
        Map<Long, Event> eventMap = new HashMap<>();
        events.forEach(e -> eventMap.put(e.getId(), e));
        List<Long> eventIds = new ArrayList<>(eventMap.keySet());
        List<ViewStatsDto> views = statisticsService.getSomeViews(epochStart, epochEnd, eventIds, uri, false);
        views.forEach(v -> eventMap.get(v.getIdFromUri()).setViews(v.getHits() == null ? 0L : v.getHits()));
    }

    private List<Event> filterOnlyAvailable(List<Event> events) {
        return events.stream().filter(
                        e -> e.getConfirmedRequests() < e.getParticipantLimit() || e.getParticipantLimit() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users,
                                               List<EventState> states,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size) {
        if (users != null) {
            validateUsers(users);
        }
        if (states != null) {
            validateEventStates(states);
        }
        if (categories != null) {
            validateCategories(categories);
        }
        List<Event> events =
                findSortedByEventDate(users, null, categories, null, rangeStart, rangeEnd, from, size, states);
        pullConfirmsToEvents(events);
        pullStatsToEvents(events);
        return EventMapper.toEventFullDtoList(events);
    }

    @Override
    @Transactional
    public EventFullDto publishEventByAdmin(Long eventId) {
        Event event = findById(eventId);
        validateEventForPublish(event);
        pullConfirmsToEvents(List.of(event));
        pullStatsToEvents(List.of(event));
        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventRepository.save(event);
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto rejectEventByAdmin(Long eventId) {
        Event event = findById(eventId);
        validateEventForReject(event);
        pullConfirmsToEvents(List.of(event));
        pullStatsToEvents(List.of(event));
        event.setState(EventState.CANCELED);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventRepository.save(event);
        return eventFullDto;
    }

    private void validateEventForReject(Event event) {
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Event can not be rejected due to it had been published before.");
        }
    }

    private void validateEventForPublish(Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1L))) {
            throw new ValidationException("Event can not be published less than 1 hour before its date");
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Event state for publishing can not be anything than pending");
        }
    }

    private void validateEventStates(List<EventState> states) {
        states.forEach(s -> {
            List<EventState> values = Arrays.asList(EventState.values());
            if (!values.contains(s)) {
                throw new ObjectNotFoundException("Event state that specified by admin in his list does not exist");
            }
        });

    }

    private void validateCategories(List<Long> categories) {
        categories.forEach(id -> {
            if (!categoryRepository.existsById(id)) {
                throw new ObjectNotFoundException("Category does not found in specified by admin category id list");
            }
        });
    }

    private void validateUsers(List<Long> users) {
        users.forEach(id -> {
            if (!userRepository.existsById(id)) {
                throw new ObjectNotFoundException("User does not found in specified by admin user id list");
            }
        });
    }

    private Event findById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new ObjectNotFoundException("Event not found"));
    }

    private void validatePublicAccess(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new AccessException("Access denied");
        }
    }

    private List<RequestCount> countParticipationRequests(List<Long> eventIds, RequestStatus requestStatus) {
        return participationRequestRepository.fetchRequestCountsByEventIdAndStatus(eventIds, requestStatus);
    }

    private List<Event> findSortedByEventDate(List<Long> users,
                                              String text,
                                              List<Long> categories,
                                              Boolean paid,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              Integer from,
                                              Integer size,
                                              List<EventState> states
    ) {
        Sort sort = Sort.by(Sort.Direction.ASC, "eventDate");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        return eventRepository.fetchEvents(
                users,
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                states,
                pageRequest);
    }

    @Override
    public List<EventShortDto> getSomeEventsByUser(Long userId, Integer from, Integer size) {
        List<Event> events = findSortedByEventDate(
                List.of(userId), null, null, null, null, null, from, size, null
        );
        pullConfirmsToEvents(events);
        pullStatsToEvents(events);
        return EventMapper.toEventShortDtoList(events);
    }

    @Override
    @Transactional
    public EventFullDto postEventByUser(Long userId, NewEventDto newEventDto) {
        validateEventDateForPosting(newEventDto);
        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(findUserById(userId));
        event.setCategory(findCategory(newEventDto.getCategory()));
        event.setLocation(findOrCreateLocation(newEventDto.getLocation()));
        event = eventRepository.save(event);
        event.setViews(0L);
        event.setConfirmedRequests(0L);
        return EventMapper.toEventFullDto(event);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> {
                            throw new ObjectNotFoundException("Initiator user for event posting not found");
                        }
                );
    }

    private void validateEventDateForPosting(NewEventDto newEventDto) {
        LocalDateTime eventDate = newEventDto.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ValidationException("Invalid event date");
        }
    }

    private Category findCategory(Long categoryId) {
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId).orElseThrow(
                    () -> {
                        throw new ObjectNotFoundException("Category not found");
                    });
        }
        return category;
    }

    private Location findOrCreateLocation(LocationDto locationDto) {
        Location location = null;
        if (locationDto != null && locationDto.getLat() != null && locationDto.getLon() != null) {
            Optional<Location> opt = locationRepository.findLocationByLatAndLon(
                    locationDto.getLat(), locationDto.getLon());
            location = opt.orElseGet(
                    () -> locationRepository.save(LocationMapper.toLocation(locationDto)));
        }
        return location;
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        Event event = findById(eventId);
        validateInitiator(userId, event);
        pullConfirmsToEvents(List.of(event));
        pullStatsToEvents(List.of(event));
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto patchEventByUser(Long userId, UpdateEventRequest updateEventRequest) {
        Event event = findById(updateEventRequest.getEventId());
        validateInitiator(userId, event);
        validateEventStatusForUpdate(event);
        validateEventDateForUpdate(event, updateEventRequest.getEventDate());
        validateParticipantLimit(event, updateEventRequest.getParticipantLimit());
        updateEventCategory(event, updateEventRequest.getCategory());
        updateEventState(event);
        pullStatsToEvents(List.of(event));
        EventMapper.matchEvent(event, updateEventRequest);
        event = eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto putEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {
        Event event = findById(eventId);
        validateParticipantLimit(event, adminUpdateEventRequest.getParticipantLimit());
        updateEventCategory(event, adminUpdateEventRequest.getCategory());
        event.setLocation(findOrCreateLocation(adminUpdateEventRequest.getLocation()));
        pullStatsToEvents(List.of(event));
        EventMapper.matchEvent(event, adminUpdateEventRequest);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventRepository.save(event);
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto cancelEventByUser(Long userId, Long eventId) {
        Event event = findById(eventId);
        validateInitiator(userId, event);
        validateEventStatusForCancel(event);
        event.setState(EventState.CANCELED);
        pullConfirmsToEvents(List.of(event));
        pullStatsToEvents(List.of(event));
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventRepository.save(event);
        return eventFullDto;
    }

    private void validateEventStatusForCancel(Event event) {
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Only event with pending state can be cancelled");
        }
    }

    private void updateEventCategory(Event event, Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(
                    () -> {
                        throw new ObjectNotFoundException("Category for event update not found.");
                    });
            event.setCategory(category);
        }
    }

    private void updateEventState(Event event) {
        if (event.getState().equals(EventState.CANCELED)) {
            event.setState(EventState.PENDING);
        }
    }

    private void validateParticipantLimit(Event event, Integer updateLimit) {
        pullConfirmsToEvents(List.of(event));
        Long confirms = event.getConfirmedRequests();
        if (updateLimit != 0 && updateLimit < confirms.intValue()) {
            throw new ValidationException("Participation limit exceeds number of confirmed participation requests");
        }
    }

    private void validateInitiator(Long userId, Event event) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("User is not event initiator");
        }
    }

    private void validateEventStatusForUpdate(Event event) {
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Event is already published and not allowed for update");
        }
    }

    private void validateEventDateForUpdate(Event event, LocalDateTime update) {
        if (update != null && update.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ValidationException("New event date cannot be earlier than less 2 hours left for its beginning");
        }
        LocalDateTime eventDate = event.getEventDate();
        if (update == null && eventDate.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ValidationException("Event cannot be updated before than less 2 hours left for its beginning");
        }
    }
}