package ru.practicum.server;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServerController {
    private final ServerService serverService;
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/stats")
    public List<ViewStats> getStatistics(@RequestParam String start,
                                         @RequestParam String end,
                                         @RequestParam(required = false) List<String> uris,
                                         @RequestParam(defaultValue = "false") boolean unique) {
        uris = uris == null ? Collections.emptyList() : uris;
        return serverService.getStatistics(LocalDateTime.parse(start, DATE_FORMATTER),
                LocalDateTime.parse(end, DATE_FORMATTER), uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHit saveHit(@RequestBody EndpointHitDto hit) {
        return serverService.saveHit(hit);
    }
}
