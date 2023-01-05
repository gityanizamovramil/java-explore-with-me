package ru.practicum.ewm.event.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.location.mapper.LocationMapper;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;
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
@Slf4j
public class EventServiceImpl implements EventPublicService, EventPrivateService, EventAdminService {

    private final LocalDateTime epochStart = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    private final LocalDateTime epochEnd = LocalDateTime.of(2100, 12, 31, 23, 59, 59);
    private final String uri = "/events";

    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final StatisticsService statisticsService;
    @Autowired
    private final ParticipationRequestRepository participationRequestRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final LocationRepository locationRepository;
    @Autowired
    private final UserRepository userRepository;

    public EventServiceImpl(EventRepository eventRepository,
                            StatisticsService statisticsService,
                            ParticipationRequestRepository participationRequestRepository,
                            CategoryRepository categoryRepository,
                            LocationRepository locationRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.statisticsService = statisticsService;
        this.participationRequestRepository = participationRequestRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    /*
    Получение подробной информации об опубликованном событии по его идентификатору
    */
    @Override
    @Transactional
    public EventFullDto getEventByPublic(Long eventId, HttpServletRequest request) {
        Event event = findById(eventId);
        //событие должно быть опубликовано
        validatePublicAccess(event);
        //информация о событии должна включать в себя количество подтвержденных запросов
        event.setConfirmedRequests(countParticipationRequests(eventId, RequestStatus.CONFIRMED));
        //информация о событии должна включать в себя количество просмотров
        statisticsService.getView(epochStart, epochEnd, request.getRequestURI(), false)
                .ifPresent(v -> event.setViews(v.getHits()));
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        statisticsService.makeView(request);
        return EventMapper.toEventFullDto(event);
    }

    /*
    Получение событий с возможностью фильтрации
    */
    @Override
    @Transactional
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
        //если в запросе не указан диапазон дат [rangeStart-rangeEnd],
        //то нужно выгружать события, которые произойдут позже текущей даты и времени
        rangeStart = rangeStart == null ? LocalDateTime.now() : rangeStart;
        rangeEnd = rangeEnd == null ? epochEnd : rangeEnd;
        //текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
        //это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
        //Вариант сортировки: по дате события
        log.info(String.format("get some Events by public \n " +
                        "text: %s \n " +
                        "categories: %s \n " +
                        "paid: %s \n " +
                        "rangeStart: %s \n " +
                        "rangeEnd: %s \n " +
                        "onlyAvailable: %s \n " +
                        "eventSort: %s \n " +
                        "from: %d \n " +
                        "size: %d \n ",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, eventSort, from, size));
        List<Event> events = findSortedByEventDate(
                null, text, categories, paid, rangeStart, rangeEnd, from, size, List.of(EventState.PUBLISHED)
        );
        log.info(events.toString());
        //информация о каждом событии должна включать в себя количество уже одобренных заявок на участие
        pullConfirmsToEvents(events);
        //только события у которых не исчерпан лимит запросов на участие
        if (onlyAvailable) events = filterOnlyAvailable(events);
        //информация о каждом событии должна включать в себя количество просмотров
        pullStatsToEvents(events);
        //Вариант сортировки: по количеству просмотров
        if (eventSort != null && eventSort.equals(EventSort.VIEWS)) events = sortByViews(events);
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        statisticsService.makeView(request);
        return EventMapper.toEventShortDtoList(events);
    }

    private List<Event> sortByViews(List<Event> events) {
        return events.stream().sorted(Comparator.comparing(Event::getViews)).collect(Collectors.toList());
    }

    private void pullConfirmsToEvents(List<Event> events) {
        events.forEach(e -> e.setConfirmedRequests(
                countParticipationRequests(e.getId(), RequestStatus.CONFIRMED) == null ?
                        0L : countParticipationRequests(e.getId(), RequestStatus.CONFIRMED)));
    }

    private void pullStatsToEvents(List<Event> events) {
        Map<Long, Event> eventMap = new HashMap<>();
        events.forEach(e -> {
            eventMap.put(e.getId(), e);
        });
        List<Long> eventIds = new ArrayList<>(eventMap.keySet());
        List<ViewStatsDto> views = statisticsService.getSomeViews(epochStart, epochEnd, eventIds, uri, false);
        views.forEach(v -> {
            eventMap.get(v.getIdFromUri()).setViews(v.getHits() == null ? 0L : v.getHits());
        });
    }

    private List<Event> filterOnlyAvailable(List<Event> events) {
        return events.stream().filter(
                        e -> e.getConfirmedRequests() < e.getParticipantLimit() || e.getParticipantLimit() == 0)
                .collect(Collectors.toList());
    }

    /*
    Поиск событий
    Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия
     */
    @Override
    @Transactional
    public List<EventFullDto> getEventsByAdmin(List<Long> users,
                                               List<EventState> states,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size) {
        if (users != null) validateUsers(users);
        if (states != null) validateEventStates(states);
        if (categories != null) validateCategories(categories);
        List<Event> events =
                findSortedByEventDate(users, null, categories, null, rangeStart, rangeEnd, from, size, states);
        events.forEach(e -> e.setConfirmedRequests(countParticipationRequests(e.getId(), RequestStatus.CONFIRMED)));
        pullStatsToEvents(events);
        return EventMapper.toEventFullDtoList(events);
    }

    /*
    Публикация события
     */
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

    /*
    Отклонение события
     */
    @Override
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
        //Обратите внимание: событие не должно быть опубликовано.
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("Event can not be rejected due to it had been published before.");
        }
    }

    private void validateEventForPublish(Event event) {
        //дата начала события должна быть не ранее чем за час от даты публикации.
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1L))) {
            throw new BadRequestException("Event can not be published less than 1 hour before its date");
        }
        //событие должно быть в состоянии ожидания публикации
        if (!event.getState().equals(EventState.PENDING)) {
            throw new BadRequestException("Event state for publishing can not be anything than pending");
        }
    }

    private void validateEventStates(List<EventState> states) {
        states.forEach(s -> {
            List<EventState> values = Arrays.asList(EventState.values());
            if (!values.contains(s)) {
                throw new NotFoundException("Event state that specified by admin in his list does not exist");
            }
        });

    }

    private void validateCategories(List<Long> categories) {
        categories.forEach(id -> {
            categoryRepository.findById(id).orElseThrow(
                    () -> {
                        throw new NotFoundException("Category does not found in specified by admin category id list");
                    }
            );
        });
    }

    private void validateUsers(List<Long> users) {
        users.forEach(id -> {
            userRepository.findById(id).orElseThrow(
                    () -> {
                        throw new NotFoundException("User does not found in specified by admin user id list");
                    }
            );
        });
    }

    private Event findById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
    }

    private void validatePublicAccess(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) throw new ForbiddenException("Access denied");
    }

    private Long countParticipationRequests(Long eventId, RequestStatus requestStatus) {
        return participationRequestRepository.countByEvent_IdAndStatus(eventId, requestStatus);
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

    /*
    Получение событий, добавленных текущим пользователем
     */
    @Override
    public List<EventShortDto> getSomeEventsByUser(Long userId, Integer from, Integer size) {
        List<Event> events = findSortedByEventDate(
                List.of(userId), null, null, null, null, null, from, size, null
        );
        pullConfirmsToEvents(events);
        pullStatsToEvents(events);
        return EventMapper.toEventShortDtoList(events);
    }

    /*
    Добавление нового события.
     */
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
                            throw new NotFoundException("Initiator user for event posting not found");
                        }
                );
    }

    private void validateEventDateForPosting(NewEventDto newEventDto) {
        //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
        LocalDateTime eventDate = newEventDto.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2L))) throw new BadRequestException("Invalid event date");
    }

    private Category findCategory(Long categoryId) {
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId).orElseThrow(
                    () -> {
                        throw new NotFoundException("Category not found");
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

    /*
    Получение полной информации о событии добавленном текущим пользователем
     */
    @Override
    @Transactional
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        Event event = findById(eventId);
        validateInitiator(userId, event);
        event.setConfirmedRequests(countParticipationRequests(event.getId(), RequestStatus.CONFIRMED));
        statisticsService.getView(epochStart, epochEnd, makeUri(eventId), false)
                .ifPresent(views -> event.setViews(views.getHits()));

        return EventMapper.toEventFullDto(event);
    }

    /*
    Изменение события добавленного текущим пользователем
     */
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
        EventMapper.matchEvent(event, updateEventRequest);
        event = eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    /*
    Редактирование события
    Редактирование данных любого события администратором. Валидация данных не требуется.
     */
    @Override
    public EventFullDto putEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {
        Event event = findById(eventId);
        //пропущено validateEventStatusForUpdate();
        //пропущено validateEventDateForUpdate();
        validateParticipantLimit(event, adminUpdateEventRequest.getParticipantLimit());
        updateEventCategory(event, adminUpdateEventRequest.getCategory());
        //пропущено updateEventState()
        event.setLocation(findOrCreateLocation(adminUpdateEventRequest.getLocation()));
        EventMapper.matchEvent(event, adminUpdateEventRequest);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventRepository.save(event);
        return eventFullDto;
    }

    /*
    Отмена события добавленного текущим пользователем.
     */
    @Override
    @Transactional
    public EventFullDto cancelEventByUser(Long userId, Long eventId) {
        Event event = findById(eventId);
        validateInitiator(userId, event);
        validateEventStatusForCancel(event);
        event.setState(EventState.CANCELED);
        event.setConfirmedRequests(countParticipationRequests(eventId, RequestStatus.CONFIRMED));
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventRepository.save(event);
        return eventFullDto;
    }

    private void validateEventStatusForCancel(Event event) {
        //Обратите внимание: Отменить можно только событие в состоянии ожидания модерации.
        if (!event.getState().equals(EventState.PENDING)) {
            throw new BadRequestException("Only event with pending state can be cancelled");
        }
    }

    private void updateEventCategory(Event event, Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(
                    () -> {
                        throw new NotFoundException("Category for event update not found.");
                    });
            event.setCategory(category);
        }

    }

    private void updateEventState(Event event) {
        //если редактируется отменённое событие, то оно автоматически переходит в состояние ожидания модерации
        if (event.getState().equals(EventState.CANCELED)) event.setState(EventState.PENDING);
    }

    private void validateParticipantLimit(Event event, Integer updateLimit) {
        Long confirms = countParticipationRequests(event.getId(), RequestStatus.CONFIRMED);
        if (updateLimit != 0 && updateLimit < confirms.intValue()) {
            throw new BadRequestException("Participation limit exceeds number of confirmed participation requests");
        }
    }

    private String makeUri(Long eventId) {
        return new StringBuilder().append(uri).append("/").append(eventId).toString();
    }

    private void validateInitiator(Long userId, Event event) {
        if (!event.getInitiator().getId().equals(userId)) throw new BadRequestException("User is not event initiator");
    }

    private void validateEventStatusForUpdate(Event event) {
        //изменить можно только отмененные события или события в состоянии ожидания модерации
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("Event is already published and not allowed for update");
        }
    }

    private void validateEventDateForUpdate(Event event, LocalDateTime update) {
        //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
        if (update != null && update.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new BadRequestException("New event date cannot be earlier than less 2 hours left for its beginning");
        }
        LocalDateTime eventDate = event.getEventDate();
        if (update == null && eventDate.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new BadRequestException("Event cannot be updated before than less 2 hours left for its beginning");
        }
    }


}
