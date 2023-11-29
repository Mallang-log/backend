package com.mallang.auth.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.exception.NotMatchPasswordException;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Password {

    private String encryptedPassword;

    public Password(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public void validatePassword(String rawPassword, PasswordEncoder encoder) {
        if (!encoder.match(rawPassword, encryptedPassword)) {
            throw new NotMatchPasswordException();
        }
    }
}
