package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;

import java.util.List;
import java.util.Map;

@Component
public class StatsClient {
    protected final RestClient rest;

    public StatsClient(@Value("${STATS_SERVER_URL}") String serverUrl) {
        rest = RestClient.create(serverUrl);
    }

    public ResponseEntity<Object> saveHit(EndpointHitDto hit) {
        return rest.post()
                .uri("/hit")
                .body(hit)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<List<ViewStats>> getStatistics(Map<String, Object> parameters) {
        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        parameters.forEach((key, value) -> {
            queryParams.add(key, String.valueOf(value));
        });

        return rest.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParams(queryParams)
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ViewStats>>() {
                });
    }

}
