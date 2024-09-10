package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.user.NewUserRequest;
import ru.practicum.mainservice.model.user.User;
import ru.practicum.mainservice.model.user.UserDto;
import ru.practicum.mainservice.model.user.UserMapper;
import ru.practicum.mainservice.repository.UsersRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {
    private final UsersRepository repository;
    private final UserMapper mapper;

    public List<UserDto> getAllUsers(List<Integer> ids, Integer from, Integer size) {
        Sort sortDyId = Sort.by(Sort.Direction.ASC, "id");
        int startPage = from > 0 ? (from / size) : 0;
        Pageable pageable = PageRequest.of(startPage, size, sortDyId);

        if (ids == null) {
            return repository.findAll(pageable)
                    .stream()
                    .map(mapper::toUserDto)
                    .collect(Collectors.toList());
        }
        return repository.findByIdIn(List.copyOf(ids), pageable)
                .stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(NewUserRequest newUserRequest) {
        checkEmail(newUserRequest.getEmail());
        return mapper.toUserDto(repository.save(mapper.toUser(newUserRequest)));
    }

    public void deleteUser(Long userId) {
        validateUser(userId);
        repository.deleteById(userId);
    }

    private void validateUser(Long userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User with id = " + userId + " was not found");
        }
    }

    private void checkEmail(String email) {
        List<User> user = repository.findByEmail(email);
        if (!user.isEmpty()) {
            throw new ConflictException("Email не может повторяться.");
        }
    }
}
