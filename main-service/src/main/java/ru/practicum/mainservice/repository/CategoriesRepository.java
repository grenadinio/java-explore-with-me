package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.model.category.Category;

import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByName(String name);
}
