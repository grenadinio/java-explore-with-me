package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.model.util.Location;

@Repository
public interface LocationsRepository extends JpaRepository<Location, Long> {
}
