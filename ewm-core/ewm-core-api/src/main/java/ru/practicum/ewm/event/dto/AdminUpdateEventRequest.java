package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.common.LocationDto;
import ru.practicum.ewm.common.Pattern;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminUpdateEventRequest {
    private String annotation;

    private Long category;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String title;

}
