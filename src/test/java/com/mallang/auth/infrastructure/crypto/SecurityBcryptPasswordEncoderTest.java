package com.mallang.auth.infrastructure.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.auth.domain.Password;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("스프링 시큐리티 Bcrypt 비밀번호 암호화기 (SecurityBcryptPasswordEncoder) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class SecurityBcryptPasswordEncoderTest {

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");

    private final SecurityBcryptPasswordEncoder passwordEncoder
            = new SecurityBcryptPasswordEncoder();

    @Test
    void 비밀번호를_Bcrypt_방식으로_암호화한다() {
        // given
        Password encode = passwordEncoder.encode("1234");
        Password same = passwordEncoder.encode("1234");

        // when & then
        assertThat(encode.getEncryptedPassword())
                .containsPattern(BCRYPT_PATTERN)
                .isNotEqualTo(same.getEncryptedPassword());
    }

    @Test
    void 비밀번호의_일치_여부를_확인한다() {
        // given
        Password encode = passwordEncoder.encode("1234");

        // when & then
        assertThat(passwordEncoder.match("1234", encode.getEncryptedPassword()))
                .isTrue();
    }
}
