package com.mallang.common.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

@DisplayName("URI, HttpMethod, RequestParam 조건(UriAndMethodAndParamCondition) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class UriAndMethodAndParamConditionTest {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @Test
    void URI_HttpMethod_RequestParam이_모두_조건에_맞아야_매치된다() {
        // given
        UriAndMethodAndParamCondition condition = UriAndMethodAndParamCondition.builder()
                .uriPatterns(Set.of("/test/**"))
                .httpMethods(Set.of(HttpMethod.GET, HttpMethod.POST))
                .params(Map.of("name", "mallang"))
                .build();
        given(request.getRequestURI()).willReturn("/test/sample");
        given(request.getMethod()).willReturn(HttpMethod.POST.name());
        given(request.getParameter("name")).willReturn("mallang");

        // when
        boolean match = condition.match(antPathMatcher, request);

        // then
        assertThat(match).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void RequestParam_은_아무_조건이_없다면_통과된다(Map<String, String> params) {
        // given
        UriAndMethodAndParamCondition condition = UriAndMethodAndParamCondition.builder()
                .uriPatterns(Set.of("/test/**"))
                .httpMethods(Set.of(HttpMethod.GET, HttpMethod.POST))
                .params(params)
                .build();
        given(request.getRequestURI()).willReturn("/test/sample");
        given(request.getMethod()).willReturn(HttpMethod.POST.name());

        // when
        boolean match = condition.match(antPathMatcher, request);

        // then
        assertThat(match).isTrue();
    }

    @Test
    void URI_가_조건에_맞지_않는_경우_매치_실패() {
        // given
        UriAndMethodAndParamCondition condition = UriAndMethodAndParamCondition.builder()
                .uriPatterns(Set.of("/test/**"))
                .httpMethods(Set.of(HttpMethod.GET, HttpMethod.POST))
                .params(Map.of("name", "mallang"))
                .build();
        given(request.getRequestURI()).willReturn("/tst/sample");
        given(request.getMethod()).willReturn(HttpMethod.POST.name());
        given(request.getParameter("name")).willReturn("mallang");

        // when
        boolean match = condition.match(antPathMatcher, request);

        // then
        assertThat(match).isFalse();
    }

    @Test
    void Method_가_조건에_맞지_않는_경우_매치_실패() {
        // given
        UriAndMethodAndParamCondition condition = UriAndMethodAndParamCondition.builder()
                .uriPatterns(Set.of("/test/**"))
                .httpMethods(Set.of(HttpMethod.GET, HttpMethod.POST))
                .params(Map.of("name", "mallang"))
                .build();
        given(request.getRequestURI()).willReturn("/test/sample");
        given(request.getMethod()).willReturn(HttpMethod.DELETE.name());
        given(request.getParameter("name")).willReturn("mallang");

        // when
        boolean match = condition.match(antPathMatcher, request);

        // then
        assertThat(match).isFalse();
    }

    @Test
    void Param_이_조건에_맞지_않는_경우_매치_실패() {
        // given
        UriAndMethodAndParamCondition condition = UriAndMethodAndParamCondition.builder()
                .uriPatterns(Set.of("/test/**"))
                .httpMethods(Set.of(HttpMethod.GET, HttpMethod.POST))
                .params(Map.of("name", "mallang"))
                .build();
        given(request.getRequestURI()).willReturn("/test/sample");
        given(request.getMethod()).willReturn(HttpMethod.POST.name());
        given(request.getParameter("name")).willReturn("wrong");

        // when
        boolean match = condition.match(antPathMatcher, request);

        // then
        assertThat(match).isFalse();
    }
}
