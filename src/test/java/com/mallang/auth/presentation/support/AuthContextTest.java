package com.mallang.auth.presentation.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.auth.exception.IncorrectUseAuthAtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("인증 정보 저장 컨텍스트 (AuthContext) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthContextTest {

    @Test
    void 인증_여부를_반환한다() {
        // given
        AuthContext unauthContext = new AuthContext();
        AuthContext authContext = new AuthContext();

        // when
        authContext.setMemberId(1L);

        // then
        assertThat(unauthContext.unAuthenticated()).isTrue();
        assertThat(authContext.unAuthenticated()).isFalse();
    }

    @Test
    void 인증정보가_없을때_인증정보를_조회하는_경우_예외() {
        // given
        AuthContext unauthContext = new AuthContext();
        AuthContext authContext = new AuthContext();
        authContext.setMemberId(1L);

        // when & then
        assertThatThrownBy(() ->
                unauthContext.getMemberId()
        ).isInstanceOf(IncorrectUseAuthAtException.class);
        assertThat(authContext.getMemberId()).isEqualTo(1L);
    }
}
