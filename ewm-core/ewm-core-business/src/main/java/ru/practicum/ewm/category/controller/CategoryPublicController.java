package ru.practicum.ewm.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryPublicService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@Validated
public class CategoryPublicController {

    @Autowired
    private final CategoryPublicService categoryPublicService;

    public CategoryPublicController(CategoryPublicService categoryPublicService) {
        this.categoryPublicService = categoryPublicService;
    }

    /*
    Получение категорий
     */
    @GetMapping
    public List<CategoryDto> getSomeCategoriesByPublic(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return categoryPublicService.getSome(from, size);
    }

    /*
    Получение информации о категории по её идентификатору
     */
    @GetMapping("/{catId}")
    public CategoryDto getCategoryByPublic(
            @PathVariable @NotNull @Positive Long catId
    ) {
        return categoryPublicService.getById(catId);
    }
}
