package com.mallang.auth.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.auth.exception.DuplicateUsernameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("일반 회원 (BasicMember) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BasicMemberTest {

    @Nested
    class 회원가입_시 {

        private final BasicMemberValidator basicMemberValidator = mock(BasicMemberValidator.class);
        private final BasicMember member = new BasicMember(
                "nickname",
                "profile",
                "username",
                "password"
        );

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
}
