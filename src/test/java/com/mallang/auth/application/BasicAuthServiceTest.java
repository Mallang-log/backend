package com.mallang.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.auth.application.command.BasicSignupCommand;
import com.mallang.auth.domain.BasicMember;
import com.mallang.auth.exception.DuplicateUsernameException;
import com.mallang.auth.exception.NotFoundMemberException;
import com.mallang.auth.exception.NotMatchPasswordException;
import com.mallang.common.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("일반 회원 서비스 (BasicAuthService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BasicAuthServiceTest extends ServiceTest {

    private final BasicSignupCommand signupCommand = new BasicSignupCommand(
            "mallang",
            "password",
            "mallang",
            "image"
    );

    @Nested
    class 회원가입_시 {

        @Test
        void 아이디가_중복되면_예외() {
            // given
            basicAuthService.signup(signupCommand);

            // when & then
            assertThatThrownBy(() ->
                    basicAuthService.signup(signupCommand)
            ).isInstanceOf(DuplicateUsernameException.class);
        }

        @Test
        void 중복되는_아이디가_없는_경우_가입된다() {
            // when
            Long id = basicAuthService.signup(signupCommand);

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 비밀번호는_암호화되어_원문을_찾을_수_없다() {
            // given
            Long id = basicAuthService.signup(signupCommand);
            BasicMember saved = (BasicMember) memberRepository.getById(id);

            // when
            assertThat(saved.getPassword()).isNotEqualTo("password");
        }
    }

    @Nested
    class 로그인_시 {

        @BeforeEach
        void setUp() {
            basicAuthService.signup(signupCommand);
        }

        @Test
        void 아이디와_비밀번호가_일치하면_로그인_성공() {
            // when
            Long id = basicAuthService.login(signupCommand.username(), signupCommand.password());

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 아이디가_없다면_예외() {
            // when & then
            assertThatThrownBy(() -> {
                basicAuthService.login(
                        signupCommand.username() + "wrong",
                        signupCommand.password()
                );
            }).isInstanceOf(NotFoundMemberException.class);
        }

        @Test
        void 비밀번호가_다르다면_예외() {
            // when & then
            assertThatThrownBy(() -> {
                basicAuthService.login(
                        signupCommand.username(),
                        signupCommand.password() + "wrong"
                );
            }).isInstanceOf(NotMatchPasswordException.class);
        }
    }
}
