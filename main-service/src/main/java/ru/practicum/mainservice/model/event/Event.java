package ru.practicum.mainservice.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mainservice.model.category.Category;
import ru.practicum.mainservice.model.user.User;
import ru.practicum.mainservice.model.util.Location;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;

    @Column(name = "created_on")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @Column(name = "event_date")
    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "location")
    private Location location;

    @NotNull
    private Boolean paid;

    @Column(name = "participants_limit")
    @Min(value = 0)
    private Integer participantLimit;

    @Column(name = "published_on")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    @NotNull
    private Boolean requestModeration;

    private String state;

    @NotBlank
    @Length(min = 3, max = 120)
    private String title;
}
