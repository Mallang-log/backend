package com.mallang.statistics.collector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mallang.common.ServiceTest;
import com.mallang.post.query.PostQueryService;
import com.mallang.post.query.dao.PostSearchDao.PostSearchCond;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@DisplayName("포스트 조회 이력 AOP(PostViewHistoryAop) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
class PostViewHistoryAopTest extends ServiceTest {

    @MockBean
    private PostViewHistoryCollector postViewHistoryCollector;

    @Autowired
    private PostQueryService postQueryService;

    @Autowired
    private PostViewHistoryAop postViewHistoryAop;

    @Test
    void forJacocoTestCoverage() {
        postViewHistoryAop.postQueryServiceGetById();
    }

    @Test
    void 포인트컷으로_PostQueryService_의_getById_매칭() throws NoSuchMethodException {
        // given
        Method pointcut = PostViewHistoryAop.class.getDeclaredMethod("postQueryServiceGetById");
        Pointcut annotation = AnnotationUtils.getAnnotation(pointcut, Pointcut.class);
        String regex = "execution\\(\\* (.+?)\\(..\\)\\)";
        Pattern pattern = Pattern.compile(regex);
        assert annotation != null;
        Matcher matcher = pattern.matcher(annotation.value());

        // when
        matcher.find();
        String group = matcher.group(1);

        // then
        assertThat(group).isEqualTo(PostQueryService.class.getName() + ".getById");
    }

    @Test
    void PostQueryService_의_getById_외의_메서드가_호출되면_저장하지_않는다() {
        // when
        postQueryService.search(null,
                new PostSearchCond(null, null, null, null, null, null, null),
                PageRequest.of(0, 20));

        // then
        verify(postViewHistoryCollector, times(0)).save(any());
    }

    @Test
    void PostQueryService_getById_시_기존_조회수_쿠키가_없다면_세팅한다() {
        // given
        Long memberId = 회원을_저장한다("말랑");
        Long blogId = 블로그_개설(memberId, "mallang-log");
        Long postId = 포스트를_저장한다(memberId, blogId, "안녕", "내용");

        // when
        postQueryService.getById(memberId, null, postId);

        // then
        verify(postViewHistoryCollector, times(1)).save(any());
        MockHttpServletResponse response = testHttpResponse();
        assertThat(response.getCookie("POST_VIEW_COOKIE")).isNotNull();
    }

    @Test
    void 세팅된_쿠키는_httpOnly_secure_10년뒤_만료이다() {
        // given
        Long memberId = 회원을_저장한다("말랑");
        Long blogId = 블로그_개설(memberId, "mallang-log");
        Long postId = 포스트를_저장한다(memberId, blogId, "안녕", "내용");
        postQueryService.getById(memberId, null, postId);

        // when
        Cookie postViewCookie = testHttpResponse().getCookie("POST_VIEW_COOKIE");

        // then
        assert postViewCookie != null;
        assertThat(postViewCookie.isHttpOnly()).isTrue();
        assertThat(postViewCookie.getSecure()).isTrue();
        assertThat(postViewCookie.getMaxAge()).isEqualTo(60 * 60 * 24 * 365 * 10);

    }

    @Test
    void PostQueryService_getById_시_기존_조회수_쿠키가_있다면_별다른_설정을_하지_않는다() {
        // given
        MockHttpServletRequest request = testHttpRequest();
        request.setCookies(new Cookie("POST_VIEW_COOKIE", UUID.randomUUID().toString()));
        Long memberId = 회원을_저장한다("말랑");
        Long blogId = 블로그_개설(memberId, "mallang-log");
        Long postId = 포스트를_저장한다(memberId, blogId, "안녕", "내용");

        // when
        postQueryService.getById(memberId, null, postId);

        // then
        verify(postViewHistoryCollector, times(1)).save(any());
        MockHttpServletResponse response = testHttpResponse();
        assertThat(response.getCookie("POST_VIEW_COOKIE")).isNull();
    }

    @Test
    void 조회_실패시_동작하지_않는다() {
        // when
        assertThatThrownBy(() -> {
            postQueryService.getById(1L, null, 1000L);
        });

        // then
        verify(postViewHistoryCollector, times(0)).save(any());
        MockHttpServletResponse response = testHttpResponse();
        assertThat(response.getCookie("POST_VIEW_COOKIE")).isNull();
    }

    private MockHttpServletRequest testHttpRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return (MockHttpServletRequest) request;
    }

    private MockHttpServletResponse testHttpResponse() {
        HttpServletResponse servletResponse = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        return (MockHttpServletResponse) servletResponse;
    }
}
