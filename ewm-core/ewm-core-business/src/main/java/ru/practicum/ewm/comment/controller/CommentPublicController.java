package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.service.CommentPublicService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/events/{eventId}/comments")
@Validated
@RequiredArgsConstructor
public class CommentPublicController {
    private final CommentPublicService commentPublicService;

    /*
    Получение опубликованных комментариев к событию
     */
    @GetMapping
    public List<CommentShortDto> getSomeCommentsByPublic(
            @PathVariable @NotNull @Positive Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return commentPublicService.getSomeCommentsByPublic(eventId, from, size);
    }

    /*
    Получение опубликованного комментария по его идентификатору
     */
    @GetMapping("/{commentId}")
    public CommentShortDto getCommentByPublic(
            @PathVariable @NotNull @Positive Long eventId,
            @PathVariable @NotNull @Positive Long commentId
    ) {
        return commentPublicService.getCommentByPublic(eventId, commentId);
    }
}
