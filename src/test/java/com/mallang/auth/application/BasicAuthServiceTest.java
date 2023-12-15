package com.mallang.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.auth.application.command.BasicSignupCommand;
import com.mallang.auth.domain.BasicMember;
import com.mallang.auth.domain.BasicMemberRepository;
import com.mallang.auth.domain.BasicMemberValidator;
import com.mallang.auth.domain.Password;
import com.mallang.auth.domain.PasswordEncoder;
import com.mallang.auth.exception.DuplicateUsernameException;
import com.mallang.auth.exception.NotFoundMemberException;
import com.mallang.auth.exception.NotMatchPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("일반 회원 서비스 (BasicAuthService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BasicAuthServiceTest {

    private final BasicMemberRepository basicMemberRepository = mock(BasicMemberRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final BasicMemberValidator validator = mock(BasicMemberValidator.class);
    private final BasicAuthService basicAuthService = new BasicAuthService(
            basicMemberRepository,
            passwordEncoder,
            validator
    );

    private final BasicMember member = new BasicMember(
            "username",
            new Password("encoded"),
            "mallang",
            "profileImage"
    );

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(member, "id", 1L);
    }

    @Nested
    class 회원가입_시 {

        private final BasicSignupCommand signupCommand = new BasicSignupCommand(
                "mallang",
                "password",
                "mallang",
                "profileImage"
        );

        @Test
        void 아이디가_중복되면_예외() {
            // given
            willThrow(DuplicateUsernameException.class)
                    .given(validator)
                    .validateDuplicateUsername("mallang");

            // when & then
            assertThatThrownBy(() ->
                    basicAuthService.signup(signupCommand)
            ).isInstanceOf(DuplicateUsernameException.class);
        }

        @Test
        void 중복되는_아이디가_없는_경우_가입된다() {
            // when
            given(passwordEncoder.encode(any()))
                    .willReturn(new Password("encoded"));
            given(basicMemberRepository.save(any())).willReturn(member);
            Long id = basicAuthService.signup(signupCommand);

            // then
            assertThat(id).isNotNull();
            then(passwordEncoder)
                    .should(times(1))
                    .encode("password");
        }
    }

    @Nested
    class 로그인_시 {

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(member, "id", 1L);
            given(basicMemberRepository.getByUsername(member.getUsername()))
                    .willReturn(member);
        }

        @Test
        void 아이디와_비밀번호가_일치하면_로그인_성공() {
            // given
            given(passwordEncoder.match("password", "encoded"))
                    .willReturn(true);

            // when
            Long id = basicAuthService.login("username", "password");

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 아이디가_없다면_예외() {
            // given
            willThrow(NotFoundMemberException.class)
                    .given(basicMemberRepository)
                    .getByUsername("wrong");

            // when & then
            assertThatThrownBy(() -> {
                basicAuthService.login("wrong", "password");
            }).isInstanceOf(NotFoundMemberException.class);
        }

        @Test
        void 비밀번호가_다르다면_예외() {
            // given
            given(passwordEncoder.match("username", "wrong"))
                    .willReturn(false);

            // when & then
            assertThatThrownBy(() -> {
                basicAuthService.login("username", "wrong");
            }).isInstanceOf(NotMatchPasswordException.class);
        }
    }
}
