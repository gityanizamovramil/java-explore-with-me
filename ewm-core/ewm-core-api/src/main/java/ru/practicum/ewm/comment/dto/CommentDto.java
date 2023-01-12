package ru.practicum.ewm.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.common.CommentState;
import ru.practicum.ewm.event.dto.EventPreviewDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    private Long id;
    private String authorName;
    private LocalDateTime created;
    private EventPreviewDto event;
    private CommentState state;
    private String text;

}
