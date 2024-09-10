package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.model.category.Category;

import java.util.List;

public interface CategoriesRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByName(String name);
}
