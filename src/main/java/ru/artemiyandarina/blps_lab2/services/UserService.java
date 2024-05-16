package ru.artemiyandarina.blps_lab2.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import ru.artemiyandarina.blps_lab2.repositories.UserRepository;
import ru.artemiyandarina.blps_lab2.repositories.XMLUserRepository;
import ru.artemiyandarina.blps_lab2.services.mapping.UserMapper;
import ru.artemiyandarina.blps_lab2.schemas.user.UserRead;
import ru.artemiyandarina.blps_lab2.schemas.user.UserRegister;
import ru.artemiyandarina.blps_lab2.models.User;
import ru.artemiyandarina.blps_lab2.exceptions.NotFoundException;


@Service
@RequiredArgsConstructor
public class UserService {
    final UserRepository userRepository;
    final UserMapper userMapper;
    final XMLUserRepository xmlUserRepository;
    public UserRead register(UserRegister schema) {
        User newUser = userMapper.mapUserRegistrationToEntity(schema);
        newUser = userRepository.save(newUser);
        xmlUserRepository.save(newUser);
        return userMapper.mapEntityToUserRead(newUser);
    }

    public UserRead getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(id, "User"));

        return userMapper.mapEntityToUserRead(user);
    }

    public List<UserRead> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::mapEntityToUserRead)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(id, "User"));
        userRepository.delete(user);
        xmlUserRepository.delete(user);
    }
}
