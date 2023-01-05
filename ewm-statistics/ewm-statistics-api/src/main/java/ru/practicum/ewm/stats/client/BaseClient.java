package ru.practicum.ewm.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class BaseClient {

    private final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> Optional<T> exchange(String path,
                                     HttpMethod method,
                                     @Nullable T object,
                                     ParameterizedTypeReference<T> typeReference,
                                     Map<String, Object> parameters) {
        HttpEntity<T> requestEntity = new HttpEntity<>(object, defaultHeaders());
        ResponseEntity<T> responseEntity;
        try {
            if (parameters != null) {
                responseEntity = rest.exchange(path, method, requestEntity, typeReference, parameters);
            } else {
                responseEntity = rest.exchange(path, method, requestEntity, typeReference);
            }
        } catch (HttpStatusCodeException e) {
            log.info(Arrays.toString(e.getStackTrace()));
            return Optional.empty();
        }
        log.info(responseEntity.getBody().toString());
        return Optional.ofNullable(responseEntity.getBody());
    }

    protected <T> Optional<List<T>> exchangeAsList(String path,
                                                   HttpMethod method,
                                                   @Nullable List<T> objects,
                                                   ParameterizedTypeReference<List<T>> typeReference,
                                                   Map<String, Object> parameters) {
        HttpEntity<List<T>> requestEntity = new HttpEntity<>(objects, defaultHeaders());
        ResponseEntity<List<T>> responseEntity;
        try {
            if (parameters != null) {
                responseEntity = rest.exchange(path, method, requestEntity, typeReference, parameters);
            } else {
                responseEntity = rest.exchange(path, method, requestEntity, typeReference);
            }
        } catch (HttpStatusCodeException e) {
            log.info(Arrays.toString(e.getStackTrace()));
            return Optional.empty();
        }
        log.info(responseEntity.getBody().toString());
        return Optional.ofNullable(responseEntity.getBody());
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

}
