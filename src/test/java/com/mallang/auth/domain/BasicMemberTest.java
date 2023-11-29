package com.mallang.auth.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.auth.exception.DuplicateUsernameException;
import com.mallang.auth.exception.NotMatchPasswordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("일반 회원 (BasicMember) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BasicMemberTest {

    private final BasicMemberValidator basicMemberValidator = mock(BasicMemberValidator.class);
    private final BasicMember member = new BasicMember(
            "username",
            new Password("password"),
            "nickname",
            "profile"
    );

    @Nested
    class 회원가입_시 {

        @Test
        void 아이디가_중복되지_않는다면_가입된다() {
            // when & then
            assertDoesNotThrow(() -> {
                member.signup(basicMemberValidator);
            });
        }

        @Test
        void 아이디가_중복되면_예외() {
            // given
            willThrow(DuplicateUsernameException.class)
                    .given(basicMemberValidator)
                    .validateDuplicateUsername("username");

            // when & then
            assertThatThrownBy(() -> {
                member.signup(basicMemberValidator);
            }).isInstanceOf(DuplicateUsernameException.class);
        }
    }

    @Nested
    class 로그인_시 {

        private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        @Test
        void 비밀번호가_일치하면_성공() {
            // given
            given(passwordEncoder.match("password", "password"))
                    .willReturn(true);

            // when & then
            assertDoesNotThrow(() -> {
                member.login("password", passwordEncoder);
            });
        }

        @Test
        void 비밀번호가_다르면_실패() {
            // given
            given(passwordEncoder.match("password", "password"))
                    .willReturn(false);

            // when & then
            assertThatThrownBy(() -> {
                member.login("password", passwordEncoder);
            }).isInstanceOf(NotMatchPasswordException.class);
        }
    }
}
