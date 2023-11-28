package com.mallang.statistics.statistic.collector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mallang.blog.application.command.WriteAboutCommand;
import com.mallang.blog.presentation.AboutController;
import com.mallang.common.ServiceTest;
import com.mallang.post.exception.NotFoundPostException;
import com.mallang.post.presentation.PostController;
import com.mallang.post.query.dao.PostSearchDao.PostSearchCond;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@DisplayName("블로그 방문 이력 AOP (BlogVisitHistoryAop) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
class BlogVisitHistoryAopTest extends ServiceTest {

    @MockBean
    private BlogVisitHistoryCollector blogVisitHistoryCollector;

    @Autowired
    private PostController postController;

    @Autowired
    private AboutController aboutController;

    @Autowired
    private BlogVisitHistoryAop blogVisitHistoryAop;

    @Test
    void Blog_About_Post_Controller_중_GetMapping이_붙었으며_파라미터로_blogName을_받는_메서드_외_다른_메서드가_호출되면_저장하지_않는다() {
        // when
        PostSearchCond cond = PostSearchCond.builder()
                .blogName("blog-name")
                .build();
        postController.search(null, cond, PageRequest.of(0, 100));

        // then
        verify(blogVisitHistoryCollector, times(0)).save(any());
    }

    @Test
    void 작동_시_기존_블로그_방문_쿠키가_없다면_세팅한다() {
        // given
        Long memberId = 회원을_저장한다("말랑");
        String blogName = 블로그_개설(memberId, "mallang-log");
        Long postId = 포스트를_저장한다(memberId, blogName, "안녕", "내용").getId();

        // when
        postController.getById(null, null, "mallang-log", postId);

        // then
        verify(blogVisitHistoryCollector, times(1)).save(any());
        MockHttpServletResponse response = testHttpResponse();
        assertThat(response.getCookie("BLOG_VISIT_COOKIE")).isNotNull();
    }

    @Test
    void 세팅된_쿠키는_httpOnly_secure_10년뒤_만료이다() {
        // given
        Long memberId = 회원을_저장한다("말랑");
        String blogName = 블로그_개설(memberId, "mallang-log");
        Long postId = 포스트를_저장한다(memberId, blogName, "안녕", "내용").getId();
        postController.getById(null, null, "mallang-log", postId);

        // when
        Cookie postViewCookie = testHttpResponse().getCookie("BLOG_VISIT_COOKIE");

        // then
        assertThat(postViewCookie.isHttpOnly()).isTrue();
        assertThat(postViewCookie.getSecure()).isTrue();
        assertThat(postViewCookie.getMaxAge()).isEqualTo(60 * 60 * 24 * 365 * 10);

    }

    @Test
    void 작동_시_기존_블로그_방문_쿠키가_있다면_별다른_설정을_하지_않는다() {
        // given
        MockHttpServletRequest request = testHttpRequest();
        request.setCookies(new Cookie("BLOG_VISIT_COOKIE", UUID.randomUUID().toString()));
        Long memberId = 회원을_저장한다("말랑");
        String blogName = 블로그_개설(memberId, "mallang-log");
        aboutService.write(new WriteAboutCommand(memberId, blogName, "about"));

        // when
        aboutController.findByBlogName("mallang-log");

        // then
        verify(blogVisitHistoryCollector, times(1)).save(any());
        MockHttpServletResponse response = testHttpResponse();
        assertThat(response.getCookie("BLOG_VISIT_COOKIE")).isNull();
    }

    @Test
    void 조회_실패시_동작하지_않는다() {
        // when
        assertThatThrownBy(() -> {
            postController.getById(null, null, "mallang-log", 1000L);
        }).isInstanceOf(NotFoundPostException.class);

        // then
        verify(blogVisitHistoryCollector, times(0)).save(any());
        MockHttpServletResponse response = testHttpResponse();
        assertThat(response.getCookie("BLOG_VISIT_COOKIE")).isNull();
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
