package ru.practicum.mainservice.model.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.mainservice.model.category.CategoryDto;
import ru.practicum.mainservice.model.user.UserShortDto;

@Getter
@Setter
@AllArgsConstructor
public class EventShortDto {
    private Long id;
    @NotBlank
    private String annotation;
    @NotNull
    private CategoryDto category;
    private Long confirmedRequests;
    @NotBlank
    private String eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private boolean paid;
    @NotBlank
    private String title;
    private Long views;
}
