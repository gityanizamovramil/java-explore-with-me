package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserAdminService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@Validated
@RequiredArgsConstructor
public class UserAdminController {
    private final UserAdminService userAdminService;

    /*
    Получение информации о пользователях
    Возвращает информацию обо всех пользователях
    (учитываются параметры ограничения выборки), либо о конкретных (учитываются указанные идентификаторы)
     */
    @GetMapping
    public List<UserDto> getSomeUsersByAdmin(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return userAdminService.getSome(ids, from, size);
    }

    /*
    Добавление нового пользователя
     */
    @PostMapping
    public UserDto postUserByAdmin(@RequestBody @Valid NewUserRequest newUserRequest) {
        return userAdminService.create(newUserRequest);
    }

    /*
    Удаление пользователя
     */
    @DeleteMapping("/{userId}")
    public void deleteUserByAdmin(@PathVariable @NotNull @Positive Long userId) {
        userAdminService.delete(userId);
    }
}
