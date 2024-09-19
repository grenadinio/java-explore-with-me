package ru.practicum.mainservice.model.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateCommentDto {
    @NotBlank
    @Length(max = 2000)
    private String text;
}
