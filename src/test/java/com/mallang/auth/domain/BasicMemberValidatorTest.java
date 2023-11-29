package com.mallang.auth.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.exception.DuplicateUsernameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("일반 회원 검증기 (BasicMemberValidator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BasicMemberValidatorTest {

    private final BasicMemberRepository basicMemberRepository = mock(BasicMemberRepository.class);
    private final BasicMemberValidator validator = new BasicMemberValidator(basicMemberRepository);

    @Test
    void 아이디_중복을_검사한다() {
        // given
        given(basicMemberRepository.existsByUsername("exists"))
                .willReturn(true);

        // when & then
        assertDoesNotThrow(() -> {
            validator.validateDuplicateUsername("mallang");
        });
        assertThatThrownBy(() -> {
            validator.validateDuplicateUsername("exists");
        }).isInstanceOf(DuplicateUsernameException.class);
    }
}
