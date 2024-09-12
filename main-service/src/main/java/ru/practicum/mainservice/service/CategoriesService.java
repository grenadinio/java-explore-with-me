package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.category.CategoriesMapper;
import ru.practicum.mainservice.model.category.Category;
import ru.practicum.mainservice.model.category.CategoryDto;
import ru.practicum.mainservice.model.category.NewCategoryDto;
import ru.practicum.mainservice.repository.CategoriesRepository;
import ru.practicum.mainservice.repository.EventsRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final EventsRepository eventsRepository;
    private final CategoriesMapper categoriesMapper;

    public CategoryDto createCategory(NewCategoryDto body) {
        checkName(body.getName());
        return categoriesMapper.toCategoryDto(categoriesRepository.save(categoriesMapper.toCategory(body)));
    }

    public void deleteCategory(Long catId) {
        validateAndGetCategory(catId);
        if (!eventsRepository.findByCategoryId(catId).isEmpty()) {
            throw new ConflictException("The category is not empty");
        }
        categoriesRepository.deleteById(catId);
    }

    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = validateAndGetCategory(catId);
        if (!Objects.equals(category.getName(), categoryDto.getName())) {
            checkName(categoryDto.getName());
        }
        category.setName(categoryDto.getName());
        return categoriesMapper.toCategoryDto(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size, sortById);
        return categoriesRepository.findAll(pageable)
                .stream()
                .map(categoriesMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long catId) {
        return categoriesMapper.toCategoryDto(validateAndGetCategory(catId));
    }

    private Category validateAndGetCategory(Long catId) {
        Optional<Category> category = categoriesRepository.findById(catId);

        if (category.isEmpty()) {
            throw new NotFoundException("Category with id = " + catId + " was not found");
        }
        return category.get();
    }

    private void checkName(String name) {
        List<Category> categoriesSameName = categoriesRepository.findAllByName(name);

        if (!categoriesSameName.isEmpty()) {
            throw new ConflictException("Имя категории не может повторяться.");
        }
    }
}
