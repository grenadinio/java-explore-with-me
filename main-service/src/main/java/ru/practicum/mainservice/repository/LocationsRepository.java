package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.model.util.Location;

public interface LocationsRepository extends JpaRepository<Location, Long> {
}
