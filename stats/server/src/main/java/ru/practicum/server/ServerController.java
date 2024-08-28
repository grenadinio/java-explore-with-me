package ru.practicum.server;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServerController {
    private final ServerService serverService;

    @GetMapping("/stats")
    public List<ViewStats> getStatistics(@RequestParam String start,
                                         @RequestParam String end,
                                         @RequestParam(required = false) List<String> uris,
                                         @RequestParam(required = false, defaultValue = "false") boolean unique) {
        uris = uris == null ? Collections.emptyList() : uris;
        return serverService.getStatistics(Timestamp.valueOf(start), Timestamp.valueOf(end), uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHit saveHit(@RequestBody EndpointHit hit) {
        return serverService.saveHit(hit);
    }
}
