package com.mallang.post.presentation.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;

@DisplayName("보호 글 비밀번호 ArgumentResolver(OptionalPostPasswordArgumentResolver) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class OptionalPostPasswordArgumentResolverTest {

    private final PostPasswordContext postPasswordContext = mock(PostPasswordContext.class);
    private final OptionalPostPasswordArgumentResolver optionalPostPasswordArgumentResolver =
            new OptionalPostPasswordArgumentResolver(postPasswordContext);

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void OptionalPostPassword_어노테이션이_불었으면서_String_타입인_파라미터에_대해_동작한다() {
        // given
        MethodParameter methodParameter = mock(MethodParameter.class);
        given(methodParameter.hasParameterAnnotation(OptionalPostPassword.class))
                .willReturn(true);
        Class clazz = String.class;
        given(methodParameter.getParameterType())
                .willReturn(clazz);

        // when
        boolean result = optionalPostPasswordArgumentResolver
                .supportsParameter(methodParameter);

        // then
        assertThat(result).isTrue();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void OptionalPostPassword_어노테이션이_없으면_동작하지_않는다() {
        // given
        MethodParameter methodParameter = mock(MethodParameter.class);
        given(methodParameter.hasParameterAnnotation(OptionalPostPassword.class))
                .willReturn(false);
        Class clazz = String.class;
        given(methodParameter.getParameterType())
                .willReturn(clazz);

        // when
        boolean result = optionalPostPasswordArgumentResolver
                .supportsParameter(methodParameter);

        // then
        assertThat(result).isFalse();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void String_타입이_아니면_동작하지_않는다() {
        // given
        MethodParameter methodParameter = mock(MethodParameter.class);
        given(methodParameter.hasParameterAnnotation(OptionalPostPassword.class))
                .willReturn(true);
        Class clazz = Integer.class;
        given(methodParameter.getParameterType())
                .willReturn(clazz);

        // when
        boolean result = optionalPostPasswordArgumentResolver
                .supportsParameter(methodParameter);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 보호된_포스트에_대한_비밀번호_정보가_존재하면_꺼내온다() {
        // given
        given(postPasswordContext.getPassword())
                .willReturn("1234");

        // when
        Object result = optionalPostPasswordArgumentResolver
                .resolveArgument(null, null, null, null);

        // then
        assertThat(result).isEqualTo("1234");
    }

    @Test
    void 보호된_포스트에_대한_비밀번호_정보가_없으면_null_을_반환한다() {
        // given
        given(postPasswordContext.getPassword())
                .willReturn(null);

        // when
        Object result = optionalPostPasswordArgumentResolver
                .resolveArgument(null, null, null, null);

        // then
        assertThat(result).isNull();
    }
}
