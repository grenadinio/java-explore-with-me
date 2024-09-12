package ru.practicum.mainservice.model.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class UserShortDto {
    @NotNull
    private Long id;

    @NotBlank
    @Length(min = 2, max = 250)
    private String name;
}
