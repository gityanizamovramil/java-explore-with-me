package ru.practicum.ewm.comment.mapper;

import ru.practicum.ewm.comment.dto.*;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.common.CommentState;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommentMapper {
    public static List<CommentFullDto> toCommentFullDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
    }

    public static CommentFullDto toCommentFullDto(Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .author(Optional.ofNullable(comment.getAuthor()).map(UserMapper::toUserDto).orElse(null))
                .created(comment.getCreated())
                .event(Optional.ofNullable(comment.getEvent()).map(EventMapper::toEventInfoDto).orElse(null))
                .state(comment.getState())
                .text(comment.getText())
                .build();
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(Optional.ofNullable(comment.getAuthor()).map(User::getName).orElse(null))
                .created(comment.getCreated())
                .event(Optional.ofNullable(comment.getEvent()).map(EventMapper::toEventPreviewDto).orElse(null))
                .state(comment.getState())
                .text(comment.getText())
                .build();
    }

    public static Comment toComment(NewCommentDto newCommentDto, User author, Event event) {
        return Comment.builder()
                .id(null)
                .author(author)
                .created(LocalDateTime.now())
                .event(event)
                .state(CommentState.PENDING)
                .text(newCommentDto.getText())
                .build();
    }

    public static void matchComment(Comment comment, UpdateCommentRequest updateCommentRequest) {
        Optional.ofNullable(updateCommentRequest.getText()).ifPresent(comment::setText);
    }

    public static List<CommentShortDto> toCommentShortDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .authorName(Optional.ofNullable(comment.getAuthor()).map(User::getName).orElse(null))
                .created(comment.getCreated())
                .text(comment.getText())
                .build();
    }
}
