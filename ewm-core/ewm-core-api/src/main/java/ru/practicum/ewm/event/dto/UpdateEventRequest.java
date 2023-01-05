package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.common.Pattern;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    @Positive
    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    @NotNull
    @Positive
    private Long eventId;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    @Size(min = 3, max = 120)
    private String title;
}
