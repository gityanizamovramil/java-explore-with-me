package ru.practicum.ewm.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryAdminService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/admin/categories")
@Validated
public class CategoryAdminController {

    @Autowired
    private final CategoryAdminService categoryAdminService;

    public CategoryAdminController(CategoryAdminService categoryAdminService) {
        this.categoryAdminService = categoryAdminService;
    }

    /*
    Изменение категории
    Обратите внимание: имя категории должно быть уникальным
     */
    @PatchMapping
    public CategoryDto patchCategoryByAdmin(
            @RequestBody @Valid CategoryDto categoryDto
    ) {
        return categoryAdminService.update(categoryDto);
    }

    /*
    Добавление новой категории
    Обратите внимание: имя категории должно быть уникальным
     */
    @PostMapping
    public CategoryDto postCategoryByAdmin(
            @RequestBody @Valid NewCategoryDto newCategoryDto
    ) {
        return categoryAdminService.create(newCategoryDto);
    }

    /*
    Удаление категории
    Обратите внимание: с категорией не должно быть связано ни одного события.
     */
    @DeleteMapping("/{catId}")
    public void deleteCategoryByAdmin(
            @PathVariable @NotNull @Positive Long catId
    ) {
        categoryAdminService.delete(catId);
    }
}
