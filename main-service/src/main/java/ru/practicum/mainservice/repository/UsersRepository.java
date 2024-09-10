package ru.practicum.mainservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.model.user.User;

import java.util.List;

public interface UsersRepository extends JpaRepository<User, Long> {
    List<User> findByIdIn(List<Integer> id, Pageable pageable);

    List<User> findByEmail(String email);
}
