package ru.practicum.ewm.request.model;

import lombok.*;
import ru.practicum.ewm.common.RequestStatus;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestCount {
    private Long eventId;
    private Long participationCount;
    private RequestStatus status;
}
