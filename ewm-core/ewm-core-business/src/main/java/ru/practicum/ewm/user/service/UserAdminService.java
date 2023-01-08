package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserAdminService {

    List<UserDto> getSome(List<Long> ids, Integer from, Integer size);

    UserDto create(NewUserRequest newUserRequest);

    void delete(Long userId);
}
