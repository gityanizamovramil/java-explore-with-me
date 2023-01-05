package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.common.EventState;
import ru.practicum.ewm.common.LocationDto;
import ru.practicum.ewm.common.Pattern;
import ru.practicum.ewm.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {
    @NotBlank
    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private LocationDto location;

    @NotNull
    private Boolean paid;

    @Builder.Default
    private Integer participantLimit = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    @NotBlank
    private String title;

    private Long views;
}
