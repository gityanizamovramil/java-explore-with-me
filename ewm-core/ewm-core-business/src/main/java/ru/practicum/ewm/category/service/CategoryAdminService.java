package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

public interface CategoryAdminService {

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto create(NewCategoryDto newCategoryDto);

    void delete(Long catId);
}
