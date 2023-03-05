package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentRequest;

import java.util.List;

public interface CommentPrivateService {
    List<CommentDto> getSomeCommentsByUser(Long userId, Integer from, Integer size);

    CommentDto getCommentByUser(Long userId, Long commentId);

    CommentDto postCommentByUser(Long userId, NewCommentDto newCommentDto);

    CommentDto patchCommentByUser(Long userId, UpdateCommentRequest updateCommentRequest);

    void deleteCommentByUser(Long userId, Long commentId);
}
