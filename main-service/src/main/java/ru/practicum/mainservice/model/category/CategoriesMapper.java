package ru.practicum.mainservice.model.category;

import org.springframework.stereotype.Component;

@Component
public class CategoriesMapper {
    public Category toCategory(NewCategoryDto newCategoryDto) {
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return category;
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
