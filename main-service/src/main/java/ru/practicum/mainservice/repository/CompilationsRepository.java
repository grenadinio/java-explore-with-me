package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.model.compilation.Compilation;

public interface CompilationsRepository extends JpaRepository<Compilation, Long> {
}
