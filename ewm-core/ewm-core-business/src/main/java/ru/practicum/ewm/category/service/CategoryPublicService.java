package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.List;

public interface CategoryPublicService {

    List<CategoryDto> getSome(Integer from, Integer size);

    CategoryDto getById(Long catId);
}
