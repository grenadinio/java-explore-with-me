package ru.practicum.mainservice.model.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.mainservice.model.category.CategoryDto;
import ru.practicum.mainservice.model.user.UserShortDto;
import ru.practicum.mainservice.model.util.Location;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class EventFullDto {
    private Long id;
    @NotBlank
    private String annotation;
    @NotNull
    private CategoryDto category;
    private Long confirmedRequests;
    private String createdOn;
    private String description;
    @NotBlank
    private String eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Location location;
    @NotNull
    private boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    private String state;
    @NotBlank
    private String title;
    private Long views;
}
