package ru.practicum.mainservice.model.compilation;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.model.event.EventShortDto;

import java.util.List;

@Component
public class CompilationsMapper {
    public CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle(),
                events
        );
    }

    public Compilation toCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.getPinned());
        return compilation;
    }
}
