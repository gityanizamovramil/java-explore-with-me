package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.common.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndpointHitDto {

    /*
    Идентификатор записи
     */
    private Long id;

    /*
    Идентификатор сервиса для которого записывается информация
     */
    @NotBlank
    private String app;

    /*
    URI для которого был осуществлен запрос
     */
    @NotBlank
    private String uri;

    /*
    IP-адрес пользователя, осуществившего запрос
     */
    @NotBlank
    private String ip;

    /*
    Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Pattern.LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime timestamp;

    public static EndpointHitDto fromHttpServletRequest(HttpServletRequest request, String appName) {
        return EndpointHitDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
