package ru.practicum.mainservice.model.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;

    @NotBlank
    @Length(max = 2000)
    private String text;

    private Long eventId;

    private Long userId;

    private String publishedOn;
}
