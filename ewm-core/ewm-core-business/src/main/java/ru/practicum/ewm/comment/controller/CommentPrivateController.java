package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentRequest;
import ru.practicum.ewm.comment.service.CommentPrivateService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@Validated
@RequiredArgsConstructor
public class CommentPrivateController {

    private final CommentPrivateService commentPrivateService;

    /*
    Получение комментариев, добавленных текущим пользователем
     */
    @GetMapping
    public List<CommentDto> getSomeCommentsByUser(
            @PathVariable @NotNull @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return commentPrivateService.getSomeCommentsByUser(userId, from, size);
    }

    /*
    Получение комментария, добавленного текущим пользователем
     */
    @GetMapping("/{commentId}")
    public CommentDto getCommentByUser(
            @PathVariable @NotNull @Positive Long userId,
            @PathVariable @NotNull @Positive Long commentId
    ) {
        return commentPrivateService.getCommentByUser(userId, commentId);
    }

    /*
    Добавление нового комментария
     */
    @PostMapping
    public CommentDto postCommentByUser(
            @PathVariable @NotNull @Positive Long userId,
            @RequestBody @Valid NewCommentDto newCommentDto
    ) {
        return commentPrivateService.postCommentByUser(userId, newCommentDto);
    }

    /*
    Изменение комментария добавленного текущим пользователем
     */
    @PatchMapping
    public CommentDto patchCommentByUser(
            @PathVariable @NotNull @Positive Long userId,
            @RequestBody @Valid UpdateCommentRequest updateCommentRequest
    ) {
        return commentPrivateService.patchCommentByUser(userId, updateCommentRequest);
    }

    /*
    Удаление комментария добавленного текущим пользователем
     */
    @DeleteMapping("/{commentId}")
    public void deleteCommentByUser(
            @PathVariable @NotNull @Positive Long userId,
            @PathVariable @NotNull @Positive Long commentId
    ) {
        commentPrivateService.deleteCommentByUser(userId, commentId);
    }
}
