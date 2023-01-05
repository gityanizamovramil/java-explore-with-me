package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewStatsDto {

    /*
    Название сервиса
     */
    private String app;

    /*
    URI сервиса
     */
    private String uri;

    /*
    Количество просмотров
     */
    private Long hits;

    @JsonIgnore
    public Long getIdFromUri() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.uri);
        int lastIndex = sb.lastIndexOf("/");
        return Long.parseLong(sb. substring(lastIndex+1));
    }
}
