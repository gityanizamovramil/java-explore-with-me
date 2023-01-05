package ru.practicum.ewm.compilation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationPublicService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@Validated
public class CompilationPublicController {

    @Autowired
    private final CompilationPublicService compilationPublicService;

    public CompilationPublicController(CompilationPublicService compilationPublicService) {
        this.compilationPublicService = compilationPublicService;
    }

    /*
    Получение подборок событий
     */
    @GetMapping
    public List<CompilationDto> getSomeCompilationsByPublic(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return compilationPublicService.getSomeCompilationsByPublic(pinned, from, size);
    }

    /*
    Получение подборки событий по его id
     */
    @GetMapping("/{compId}")
    public CompilationDto getCompilationByPublic(
            @PathVariable @NotNull @Positive Long compId
    ) {
        return compilationPublicService.getCompilationByPublic(compId);
    }
}
