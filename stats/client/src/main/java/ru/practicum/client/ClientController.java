package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointHit;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ClientController {
    private final StatsClient client;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> hit(@RequestBody final EndpointHit hit) {
        return client.saveHit(hit);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> stats(@RequestParam String start,
                                        @RequestParam String end,
                                        @RequestParam(required = false) String uris,
                                        @RequestParam(required = false, defaultValue = "false") boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        parameters.put("unique", unique);
        parameters.put("uris", uris);

        return client.getStatistics(parameters);
    }
}
