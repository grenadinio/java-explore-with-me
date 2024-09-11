package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class ViewStats {
    @JsonProperty("app")
    private String app;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("hits")
    private Long hits;
}
