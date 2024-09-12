package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.model.compilation.Compilation;

@Repository
public interface CompilationsRepository extends JpaRepository<Compilation, Long> {
}
