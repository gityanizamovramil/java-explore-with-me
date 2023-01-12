package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.common.CommentState;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentAdminService {

    List<CommentFullDto> getCommentsByAdmin(List<Long> users,
                                            List<CommentState> states,
                                            List<Long> events,
                                            String text,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Integer from,
                                            Integer size);

    CommentFullDto publishCommentByAdmin(Long commentId);

    CommentFullDto rejectCommentByAdmin(Long commentId);
}
