package ru.practicum.ewm.category.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.category.service.CategoryAdminService;
import ru.practicum.ewm.category.service.CategoryPublicService;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryPublicService, CategoryAdminService {

    @Autowired
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        Category category = findById(categoryDto.getId());
        validateCategoryName(categoryDto.getName());
        CategoryMapper.matchCategory(category, categoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        validateCategoryName(newCategoryDto.getName());
        Category category = CategoryMapper.toCategory(newCategoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void delete(Long catId) {
        findById(catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getSome(Integer from, Integer size) {
        return CategoryMapper.toCategoryDtoList(categoryRepository.findAllBy(PageRequest.of(from / size, size)));
    }

    @Override
    public CategoryDto getById(Long catId) {
        return CategoryMapper.toCategoryDto(findById(catId));
    }

    private void validateCategoryName(String name) {
        List<Category> sameNameCategories = categoryRepository.findAllByNameIgnoreCase(name);
        if (!sameNameCategories.isEmpty()) {
            throw new ConflictException("The name of category is already in use.");
        }
    }

    private Category findById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category not found."));
    }
}
