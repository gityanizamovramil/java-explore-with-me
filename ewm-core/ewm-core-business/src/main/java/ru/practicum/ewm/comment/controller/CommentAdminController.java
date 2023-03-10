package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.service.CommentAdminService;
import ru.practicum.ewm.common.CommentState;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/comments")
@Validated
@RequiredArgsConstructor
public class CommentAdminController {
    private final CommentAdminService commentAdminService;

    /*
    Поиск комментариев (возвращает полную информацию обо всех комментариях подходящих под переданные условия)
    Вариант сортировки: по дате создания комментария.
    Текстовый поиск (по тексту комментария) ведется без учета регистра букв.
    Если не указан диапазон дат [rangeStart-rangeEnd], выгрузятся комментарии, созданные до текущей даты и времени.
     */
    @GetMapping
    public List<CommentFullDto> getCommentsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<CommentState> states,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return commentAdminService.getCommentsByAdmin(users, states, events, text, rangeStart, rangeEnd, from, size);
    }

    /*
    Публикация комментария
     */
    @PatchMapping("/{commentId}/publish")
    public CommentFullDto publishCommentByAdmin(@PathVariable @NotNull @Positive Long commentId) {
        return commentAdminService.publishCommentByAdmin(commentId);
    }

    /*
    Отклонение комментария
     */
    @PatchMapping("/{commentId}/reject")
    public CommentFullDto rejectCommentByAdmin(@PathVariable @NotNull @Positive Long commentId) {
        return commentAdminService.rejectCommentByAdmin(commentId);
    }

}
