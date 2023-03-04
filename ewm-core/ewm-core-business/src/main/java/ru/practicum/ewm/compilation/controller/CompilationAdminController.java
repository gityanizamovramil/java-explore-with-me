package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationAdminService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/admin/compilations")
@Validated
@RequiredArgsConstructor
public class CompilationAdminController {

    private final CompilationAdminService compilationAdminService;

    /*
    Добавление новой подборки
     */
    @PostMapping
    public CompilationDto postCompilationByAdmin(
            @RequestBody @Valid NewCompilationDto newCompilationDto
    ) {
        return compilationAdminService.postCompilationByAdmin(newCompilationDto);
    }

    /*
    Удаление подборки
     */
    @DeleteMapping("/{compId}")
    public void deleteCompilationByAdmin(
            @PathVariable @NotNull @Positive Long compId
    ) {
        compilationAdminService.deleteCompilationByAdmin(compId);
    }

    /*
    Удалить событие из подборки
     */
    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilationByAdmin(
            @PathVariable @NotNull @Positive Long compId,
            @PathVariable @NotNull @Positive Long eventId
    ) {
        compilationAdminService.deleteEventFromCompilationByAdmin(compId, eventId);
    }

    /*
    Добавить событие в подборку
     */
    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventToCompilationByAdmin(
            @PathVariable @NotNull @Positive Long compId,
            @PathVariable @NotNull @Positive Long eventId
    ) {
        compilationAdminService.addEventToCompilationByAdmin(compId, eventId);
    }

    /*
    Открепить подборку на главной странице
     */
    @DeleteMapping("/{compId}/pin")
    public void unpinCompilationByAdmin(
            @PathVariable @NotNull @Positive Long compId
    ) {
        compilationAdminService.unpinCompilationByAdmin(compId);
    }

    /*
    Закрепить подборку на главной странице
     */
    @PatchMapping("/{compId}/pin")
    public void pinCompilationByAdmin(
            @PathVariable @NotNull @Positive Long compId
    ) {
        compilationAdminService.pinCompilationByAdmin(compId);
    }
}
