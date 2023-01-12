package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentShortDto;

import java.util.List;

public interface CommentPublicService {

    List<CommentShortDto> getSomeCommentsByPublic(Long eventId, Integer from, Integer size);

    CommentShortDto getCommentByPublic(Long eventId, Long commentId);
}
