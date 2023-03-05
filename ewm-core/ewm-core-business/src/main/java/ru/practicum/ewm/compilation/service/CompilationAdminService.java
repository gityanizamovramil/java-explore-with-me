package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;

public interface CompilationAdminService {
    CompilationDto postCompilationByAdmin(NewCompilationDto newCompilationDto);

    void deleteCompilationByAdmin(Long compId);

    void deleteEventFromCompilationByAdmin(Long compId, Long eventId);

    void unpinCompilationByAdmin(Long compId);

    void pinCompilationByAdmin(Long compId);

    void addEventToCompilationByAdmin(Long compId, Long eventId);
}
