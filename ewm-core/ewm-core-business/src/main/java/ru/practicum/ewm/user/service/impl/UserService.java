package ru.practicum.ewm.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.user.service.UserAdminService;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserAdminService {

    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getSome(List<Long> ids, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) return UserMapper.toUserDtoList(userRepository.findAllBy(pageRequest));
        return UserMapper.toUserDtoList(userRepository.findByIdIn(ids, pageRequest));
    }

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        validateUserEmail(newUserRequest.getEmail());
        User user = UserMapper.toUser(newUserRequest);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void delete(Long userId) {
        findById(userId);
        userRepository.deleteById(userId);
    }

    private void validateUserEmail(String email) {
        Optional<User> sameEmailUser = userRepository.findUserByEmailIgnoreCase(email);
        sameEmailUser.ifPresent(user -> {
            throw new ConflictException("The email of user is already in use.");
        });
    }

    private User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
