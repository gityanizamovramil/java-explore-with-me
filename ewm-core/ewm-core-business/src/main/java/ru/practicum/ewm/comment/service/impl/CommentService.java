package ru.practicum.ewm.comment.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.*;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.comment.service.CommentAdminService;
import ru.practicum.ewm.comment.service.CommentPrivateService;
import ru.practicum.ewm.comment.service.CommentPublicService;
import ru.practicum.ewm.common.CommentState;
import ru.practicum.ewm.common.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.AccessException;
import ru.practicum.ewm.exception.IntegrityException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentAdminService, CommentPrivateService, CommentPublicService {
    private final LocalDateTime epochStart = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CommentFullDto> getCommentsByAdmin(List<Long> users,
                                                   List<CommentState> states,
                                                   List<Long> events,
                                                   String text,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Integer from,
                                                   Integer size) {
        rangeStart = rangeStart == null ? epochStart : rangeStart;
        rangeEnd = rangeEnd == null ? LocalDateTime.now() : rangeEnd;
        if (users != null) {
            validateUsers(users);
        }
        if (states != null) {
            validateCommentStates(states);
        }
        if (events != null) {
            validateEvents(events);
        }
        List<Comment> comments =
                findSortedByCreationDateDesc(users, states, events, text, rangeStart, rangeEnd, from, size);
        return CommentMapper.toCommentFullDtoList(comments);
    }

    private void validateEvents(List<Long> events) {
        events.forEach(id -> {
            if (!eventRepository.existsById(id)) {
                throw new ObjectNotFoundException("Event does not found in specified event id list");
            }
        });
    }

    private void validateCommentStates(List<CommentState> states) {
        List<CommentState> values = Arrays.asList(CommentState.values());
        states.forEach(s -> {
            if (!values.contains(s)) {
                throw new ObjectNotFoundException("Comment state that specified in list does not exist");
            }
        });
    }

    private void validateUsers(List<Long> users) {
        users.forEach(id -> {
            if (!userRepository.existsById(id)) {
                throw new ObjectNotFoundException("User does not found in specified user id list");
            }
        });
    }

    private List<Comment> findSortedByCreationDateDesc(List<Long> users,
                                                       List<CommentState> states,
                                                       List<Long> events,
                                                       String text,
                                                       LocalDateTime rangeStart,
                                                       LocalDateTime rangeEnd,
                                                       Integer from,
                                                       Integer size
    ) {
        PageRequest pageRequest = formPageSortedByCreationDateDesc(from, size);
        return commentRepository.fetchComments(users, states, events, text, rangeStart, rangeEnd, pageRequest);
    }

    private PageRequest formPageSortedByCreationDateDesc(Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return PageRequest.of(from / size, size, sort);
    }

    @Override
    @Transactional
    public CommentFullDto publishCommentByAdmin(Long commentId) {
        Comment comment = findById(commentId);
        validateEventIsPublished(comment.getEvent());
        comment.setState(CommentState.CONFIRMED);
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    private void validateEventIsPublished(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IntegrityException("Comment cannot be published for nonpublished event");
        }
    }

    private Comment findById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> {
                    throw new ObjectNotFoundException("Comment does not found");
                }
        );
    }

    @Override
    @Transactional
    public CommentFullDto rejectCommentByAdmin(Long commentId) {
        Comment comment = findById(commentId);
        comment.setState(CommentState.REJECTED);
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getSomeCommentsByUser(Long userId, Integer from, Integer size) {
        validateUsers(List.of(userId));
        List<Comment> comments = findCommentsByAuthorId(userId, from, size);
        return CommentMapper.toCommentDtoList(comments);
    }

    private List<Comment> findCommentsByAuthorId(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = formPageSortedByCreationDateDesc(from, size);
        return commentRepository.findAllByAuthorId(userId, pageRequest);
    }

    @Override
    public CommentDto getCommentByUser(Long userId, Long commentId) {
        validateUsers(List.of(userId));
        Comment comment = findById(commentId);
        validateCommentAuthor(userId, comment);
        return CommentMapper.toCommentDto(comment);
    }

    private void validateCommentAuthor(Long userId, Comment comment) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("User does not match to comment author.");
        }
    }

    @Override
    @Transactional
    public CommentDto postCommentByUser(Long userId, NewCommentDto newCommentDto) {
        User author = findUserById(userId);
        Event event = findEventById(newCommentDto.getEventId());
        validateEventIsPublished(event);
        Comment comment = CommentMapper.toComment(newCommentDto, author, event);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> {
                    throw new ObjectNotFoundException("Event not found");
                }
        );
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> {
                    throw new ObjectNotFoundException("Author user for comment posting not found");
                }
        );
    }

    @Override
    @Transactional
    public CommentDto patchCommentByUser(Long userId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = findById(updateCommentRequest.getCommentId());
        validateCommentAuthor(userId, comment);
        validateEventIsPublished(comment.getEvent());
        CommentMapper.matchComment(comment, updateCommentRequest);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = findById(commentId);
        validateCommentAuthor(userId, comment);
        validateEventIsPublished(comment.getEvent());
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentShortDto> getSomeCommentsByPublic(Long eventId, Integer from, Integer size) {
        Event event = findEventById(eventId);
        validateEventIsPublished(event);
        PageRequest pageRequest = formPageSortedByCreationDateDesc(from, size);
        List<Comment> comments =
                commentRepository.findAllByEventIdAndState(eventId, CommentState.CONFIRMED, pageRequest);
        return CommentMapper.toCommentShortDtoList(comments);
    }

    @Override
    public CommentShortDto getCommentByPublic(Long eventId, Long commentId) {
        Comment comment = findById(commentId);
        validateEventIsPublished(comment.getEvent());
        validateCommentForEvent(eventId, comment);
        validateCommentIsConfirmed(comment);
        return CommentMapper.toCommentShortDto(comment);
    }

    private void validateCommentIsConfirmed(Comment comment) {
        if (!comment.getState().equals(CommentState.CONFIRMED)) {
            throw new AccessException("Access denied. Comment does not confirmed yet.");
        }
    }

    private void validateCommentForEvent(Long eventId, Comment comment) {
        if (!comment.getEvent().getId().equals(eventId)) {
            throw new ValidationException("Comment does not match to event");
        }
    }
}
