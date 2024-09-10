package ru.practicum.mainservice.model.compilation;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompositeKeyForEventByComp implements Serializable {

    private Long compilationId;
    private Long eventId;

}