package ru.practicum.mainservice;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStats;
import ru.practicum.mainservice.exception.ClientException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConnectToStatServer {

    public static List<Long> getViews(LocalDateTime start, LocalDateTime end, String uris, boolean unique,
                                      StatsClient statisticClient) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        parameters.put("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        parameters.put("unique", unique);
        parameters.put("uris", uris);


        ResponseEntity<List<ViewStats>> response = statisticClient.getStatistics(parameters);


        if (response.getStatusCode().is4xxClientError()) {
            throw new ClientException("Bad request. Status code is: " + response.getStatusCode());
        }

        if (response.getStatusCode().is5xxServerError()) {
            throw new ClientException("Internal server error statusCode is " + response.getStatusCode());
        }

        if (response.getBody() == null) {
            throw new ClientException("Returned empty body");
        }

        List<ViewStats> statisticResponses = response.getBody();

        return statisticResponses
                .stream()
                .map(ViewStats::getHits)
                .collect(Collectors.toList());
    }

    public static String prepareUris(List<Long> ids) {
        return ids
                .stream()
                .map((id) -> "event/" + id).collect(Collectors.joining());
    }
}
