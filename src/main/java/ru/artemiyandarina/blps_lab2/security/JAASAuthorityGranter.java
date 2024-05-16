package ru.artemiyandarina.blps_lab2.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import ru.artemiyandarina.blps_lab2.exceptions.NotFoundException;
import ru.artemiyandarina.blps_lab2.models.User;
import ru.artemiyandarina.blps_lab2.repositories.UserRepository;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public class JAASAuthorityGranter implements AuthorityGranter {

    private final UserRepository userRepository;
    @Override
    public Set<String> grant(Principal principal) {
        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() -> new NotFoundException(principal.getName(), "User"));
        return Collections.singleton(user.getRole().name());
    }
}
