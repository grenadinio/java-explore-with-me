package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import ru.practicum.dto.EndpointHit;

import java.util.Map;

@Service
public class StatsClient {
    protected final RestClient rest;

    public StatsClient(@Value("${statistic-server.url}") String serverUrl) {
        rest = RestClient.create(serverUrl);
    }

    public ResponseEntity<Object> saveHit(EndpointHit hit) {
        return rest.post()
                .uri("/hit")
                .body(hit)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getStatistics(Map<String, Object> parameters) {
        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        parameters.forEach((key, value) -> {
            queryParams.add(key, String.valueOf(value));
        });

        return rest.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParams(queryParams)
                        .build())
                .retrieve()
                .toEntity(Object.class);
    }
}
