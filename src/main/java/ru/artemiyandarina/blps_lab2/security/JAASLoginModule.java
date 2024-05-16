package ru.artemiyandarina.blps_lab2.security;

import ru.artemiyandarina.blps_lab2.models.User;
import ru.artemiyandarina.blps_lab2.repositories.UserRepository;
import ru.artemiyandarina.blps_lab2.exceptions.NotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

public class JAASLoginModule implements LoginModule {

    private PasswordEncoder passwordEncoder;
    private String login;
    private boolean loginSucceeded = false;
    private Subject subject;
    private CallbackHandler callbackHandler;
    private UserRepository userRepository;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.callbackHandler = callbackHandler;
        this.subject = subject;
        this.userRepository = (UserRepository) options.get("userRepository");
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean login()  {
        var nameCallback = new NameCallback("login: ");
        var passwordCallback = new PasswordCallback("password: ", false);
        try {
            callbackHandler.handle(new Callback[] {nameCallback, passwordCallback});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new RuntimeException(e);
        }
        login = nameCallback.getName();
        String password = new String(passwordCallback.getPassword());
        Optional<User> user = userRepository.findByEmail(login);
        if (user.isPresent()) {
            loginSucceeded = passwordEncoder.matches(password, user.get().getPasswordHash());
        } else {
            return false;
        }
        return loginSucceeded;
    }

    @Override
    public boolean commit() {
        if(!loginSucceeded) return false;
        if(login == null) throw new NotFoundException("null", "User");
        Principal principal = (UserPrincipal) () -> login;
        subject.getPrincipals().add(principal);
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return false;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().removeIf(principal -> principal instanceof UserPrincipal);
        loginSucceeded = false;
        return true;
    }
}


