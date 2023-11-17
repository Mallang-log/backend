package com.mallang.auth.presentation.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.auth.exception.IncorrectUseAuthAtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

@DisplayName("인증 ArgumentResolver(AuthArgumentResolver) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthArgumentResolverTest {

    private final AuthContext authContext = mock(AuthContext.class);
    private final AuthArgumentResolver authArgumentResolver = new AuthArgumentResolver(authContext);

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void Auth_어노테이션과_Long_타입에_대해_동작한다() {
        // given
        MethodParameter methodParameter = mock(MethodParameter.class);
        given(methodParameter.hasParameterAnnotation(Auth.class))
                .willReturn(true);
        Class longClass = Long.class;
        given(methodParameter.getParameterType())
                .willReturn(longClass);

        // when
        boolean result = authArgumentResolver.supportsParameter(methodParameter);

        // then
        assertThat(result).isTrue();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void Auth_어노테이션이_없으면_동작하지_않는다() {
        // given
        MethodParameter methodParameter = mock(MethodParameter.class);
        given(methodParameter.hasParameterAnnotation(Auth.class))
                .willReturn(false);
        Class longClass = Long.class;
        given(methodParameter.getParameterType())
                .willReturn(longClass);

        // when
        boolean result = authArgumentResolver.supportsParameter(methodParameter);

        // then
        assertThat(result).isFalse();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void Long_타입이_아니면_동작하지_않는다() {
        // given
        MethodParameter methodParameter = mock(MethodParameter.class);
        given(methodParameter.hasParameterAnnotation(Auth.class))
                .willReturn(true);
        Class notlongClass = Integer.class;
        given(methodParameter.getParameterType())
                .willReturn(notlongClass);

        // when
        boolean result = authArgumentResolver.supportsParameter(methodParameter);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void resolveArgument_시_저장된_인증_정보를_꺼내온다() {
        // given
        given(authContext.getMemberId())
                .willReturn(1L);

        // when
        Long id = (Long) authArgumentResolver
                .resolveArgument(mock(MethodParameter.class), null, mock(NativeWebRequest.class), null);

        // then
        assertThat(id).isEqualTo(1L);
    }

    @Test
    void resolveArgument_시_저장된_인증정보가_없다면_예외() {
        // given
        willThrow(IncorrectUseAuthAtException.class)
                .given(authContext).getMemberId();

        // when & then
        assertThatThrownBy(() -> {
            authArgumentResolver
                    .resolveArgument(mock(MethodParameter.class), null, mock(NativeWebRequest.class), null);
        });
    }
}
