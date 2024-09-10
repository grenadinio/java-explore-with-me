package ru.practicum.mainservice.model.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    @NotBlank
    @Length(min = 1, max = 50)
    private String name;
}
