package ru.practicum.ewm.stats.client;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.common.Pattern;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class StatsClient extends BaseClient {

    public StatsClient(RestTemplate rest
            /*String serverUrl, String API_PREFIX*/
    ) {
        super(rest
                /*new RestTemplateBuilder()
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()*/
        );
    }

    public Optional<EndpointHitDto> postEndpointHit(EndpointHitDto endpointHitDto) {
        return exchange(
                "/hit",
                HttpMethod.POST,
                endpointHitDto,
                ParameterizedTypeReference.forType(TypeUtils.parameterize(EndpointHitDto.class)),
                null);
    }

    public Optional<List<ViewStatsDto>> getViewStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            Boolean unique
    ) {
        Map<String, Object> parameters = Map.of(
                "start", DateTimeFormatter.ofPattern(Pattern.LOCAL_DATE_TIME_FORMAT).format(start),
                "end", DateTimeFormatter.ofPattern(Pattern.LOCAL_DATE_TIME_FORMAT).format(end),
                "uris", uris.stream().collect(Collectors.joining(",")),
                "unique", unique
        );
        //String.join(",", uris
        return exchangeAsList(
                "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                HttpMethod.GET,
                null,
                ParameterizedTypeReference.forType(TypeUtils.parameterize(List.class, ViewStatsDto.class)),
                parameters
        );
    }


}
