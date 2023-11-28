package com.mallang.statistics.statistic.utils;

import static com.mallang.statistics.statistic.utils.HttpUtils.getHttpServletResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@DisplayName("HttpUtils 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class HttpUtilsTest {

    @Test
    void 요청_쿠키를_반환한다() {
        // given
        ServletRequestAttributes attribute = mock(ServletRequestAttributes.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("mallang", "test");
        request.setCookies(cookie);
        given(attribute.getRequest()).willReturn(request);
        RequestContextHolder.setRequestAttributes(attribute);

        // when
        Cookie[] requestCookies = HttpUtils.getRequestCookies();

        // then
        assertThat(requestCookies[0]).isEqualTo(cookie);
    }

    @Test
    void 요청_쿠키가_없다면_빈_배열() {
        // given
        ServletRequestAttributes attribute = mock(ServletRequestAttributes.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        given(attribute.getRequest()).willReturn(request);
        RequestContextHolder.setRequestAttributes(attribute);

        // when
        Cookie[] requestCookies = HttpUtils.getRequestCookies();

        // then
        assertThat(requestCookies).isEmpty();
    }

    @Test
    void 현재_HttpServletRequest_를_반환한다() {
        // given
        ServletRequestAttributes attribute = mock(ServletRequestAttributes.class);
        HttpServletRequest request = new MockHttpServletRequest();
        given(attribute.getRequest()).willReturn(request);
        RequestContextHolder.setRequestAttributes(attribute);

        // when
        HttpServletRequest actual = HttpUtils.getHttpServletRequest();

        // then
        assertThat(actual).isEqualTo(request);
    }

    @Test
    void 현재_HttpServletResponse_를_반환한다() {
        // given
        ServletRequestAttributes attribute = mock(ServletRequestAttributes.class);
        HttpServletResponse response = new MockHttpServletResponse();
        given(attribute.getResponse()).willReturn(response);
        RequestContextHolder.setRequestAttributes(attribute);

        // when
        HttpServletResponse actual = getHttpServletResponse();

        // then
        assertThat(actual).isEqualTo(response);
    }

    @Test
    void 응답이_없으면_예외_for_jacoco_coverage() {
        // when & then
        assertThatThrownBy(() ->
                getHttpServletResponse()
        ).isInstanceOf(NullPointerException.class);
    }
}
