package ru.practicum.ewm.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.IntegrityException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.user.service.UserAdminService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserAdminService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getSome(List<Long> ids, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) return UserMapper.toUserDtoList(userRepository.findAllBy(pageRequest));
        return UserMapper.toUserDtoList(userRepository.findByIdIn(ids, pageRequest));
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        validateUserEmail(newUserRequest.getEmail());
        User user = UserMapper.toUser(newUserRequest);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        existsById(userId);
        userRepository.deleteById(userId);
    }

    private void existsById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User not found");
        }
    }

    private void validateUserEmail(String email) {
        if (Boolean.TRUE.equals(userRepository.existsByEmailIgnoreCase(email))) {
            throw new IntegrityException("The email of user is already in use.");
        }
    }
}
