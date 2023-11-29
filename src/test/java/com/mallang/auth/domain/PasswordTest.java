package com.mallang.auth.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.auth.exception.NotMatchPasswordException;
import com.mallang.auth.infrastructure.crypto.SecurityBcryptPasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("비밀번호는 (Password) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PasswordTest {

    private final PasswordEncoder passwordEncoder = new SecurityBcryptPasswordEncoder();

    @Test
    void 비밀번호_일치_여부를_검증한다() {
        // given
        Password encode = passwordEncoder.encode("1234");

        // when & then
        assertThatThrownBy(() -> {
            encode.validatePassword("other", passwordEncoder);
        }).isInstanceOf(NotMatchPasswordException.class);
        assertDoesNotThrow(() -> {
            encode.validatePassword("1234", passwordEncoder);
        });
    }
}
