package ru.practicum.ewm.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.common.CommentState;
import ru.practicum.ewm.event.dto.EventInfoDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentFullDto {

    private Long id;
    private UserDto author;
    private LocalDateTime created;
    private EventInfoDto event;
    private CommentState state;
    private String text;
}
