package ru.artemiyandarina.blps_lab2.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.artemiyandarina.blps_lab2.models.User;
import ru.artemiyandarina.blps_lab2.repositories.UserRepository;
import ru.artemiyandarina.blps_lab2.exceptions.NotFoundException;
import ru.artemiyandarina.blps_lab2.exceptions.PermissionDeniedException;

@Service
@RequiredArgsConstructor
public class SecurityService {
    final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository
                .findByEmail(auth.getName())
                .orElseThrow(
                        () -> new NotFoundException(auth.getName(), "User")
                );
    }

    public void userRequired(User user) {
        if (!getCurrentUser().getId().equals(user.getId())) {
            throw new PermissionDeniedException();
        }
    }
}
