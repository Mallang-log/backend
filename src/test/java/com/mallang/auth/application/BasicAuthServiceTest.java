package com.mallang.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.auth.application.command.BasicSignupCommand;
import com.mallang.auth.domain.BasicMember;
import com.mallang.auth.exception.DuplicateUsernameException;
import com.mallang.common.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("일반 회원 서비스 (BasicAuthService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BasicAuthServiceTest extends ServiceTest {

    @Nested
    class 회원가입_시 {

        private final BasicSignupCommand command = new BasicSignupCommand(
                "mallang",
                "password",
                "mallang",
                "image"
        );

        @Test
        void 아이디가_중복되면_예외() {
            // given
            basicAuthService.signup(command);

            // when & then
            assertThatThrownBy(() ->
                    basicAuthService.signup(command)
            ).isInstanceOf(DuplicateUsernameException.class);
        }

        @Test
        void 중복되는_아이디가_없는_경우_가입된다() {
            // when
            Long id = basicAuthService.signup(command);

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 비밀번호는_암호화되어_원문을_찾을_수_없다() {
            // given
            Long id = basicAuthService.signup(command);
            BasicMember saved = (BasicMember) memberRepository.getById(id);

            // when
            assertThat(saved.getPassword()).isNotEqualTo("password");
        }
    }
}
