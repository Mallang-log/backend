package com.mallang.auth.infrastructure.crypto;

import static org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion.$2A;

import com.mallang.auth.domain.Password;
import com.mallang.auth.domain.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecurityBcryptPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder($2A, 10);

    @Override
    public Password encode(String rawPassword) {
        String encoded = passwordEncoder.encode(rawPassword);
        return new Password(encoded);
    }

    @Override
    public boolean match(String rawPassword, String encrypted) {
        return passwordEncoder.matches(rawPassword, encrypted);
    }
}
