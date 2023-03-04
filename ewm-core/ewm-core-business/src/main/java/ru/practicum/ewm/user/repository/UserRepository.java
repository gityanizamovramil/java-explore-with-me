package ru.practicum.ewm.user.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllBy(PageRequest pageRequest);

    List<User> findByIdIn(List<Long> ids, PageRequest pageRequest);

    Boolean existsByEmailIgnoreCase(String email);
}
