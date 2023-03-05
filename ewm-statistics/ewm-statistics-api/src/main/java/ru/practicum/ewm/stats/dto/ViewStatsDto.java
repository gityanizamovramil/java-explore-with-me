package ru.practicum.ewm.stats.dto;

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
    private String app;
    private String uri;
    private Long hits;

    @JsonIgnore
    public Long getIdFromUri() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.uri);
        int lastIndex = sb.lastIndexOf("/");
        return Long.parseLong(sb.substring(lastIndex + 1));
    }
}