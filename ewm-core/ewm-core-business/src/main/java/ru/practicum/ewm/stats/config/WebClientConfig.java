package ru.practicum.ewm.stats.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stats.client.StatsClient;

@Configuration
public class WebClientConfig {

    @Value("${ewm-stats-server.url}")
    String serverUrl;

    private static final String APP_NAME = "ewm-main-service";

    @Bean
    public StatsClient statsClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();

        return new StatsClient(restTemplate);
    }

}
