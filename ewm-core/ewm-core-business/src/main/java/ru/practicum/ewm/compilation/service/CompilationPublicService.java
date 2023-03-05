package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

public interface CompilationPublicService {
    List<CompilationDto> getSomeCompilationsByPublic(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationByPublic(Long compId);
}
