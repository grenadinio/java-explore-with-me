package ru.practicum.mainservice.model.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.mainservice.model.event.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationDto {
    @NotNull
    private Long id;
    @NotNull
    private boolean pinned;
    @NotBlank
    private String title;
    private List<EventShortDto> events;
}
