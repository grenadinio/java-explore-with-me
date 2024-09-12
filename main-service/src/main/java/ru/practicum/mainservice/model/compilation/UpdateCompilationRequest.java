package ru.practicum.mainservice.model.compilation;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class UpdateCompilationRequest {
    private List<Long> events;
    private Boolean pinned;
    @Length(max = 50)
    private String title;
}
