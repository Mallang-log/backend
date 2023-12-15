package com.mallang.blog.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.blog;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.application.command.BlogSubscribeCommand;
import com.mallang.blog.application.command.BlogUnsubscribeCommand;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.blog.domain.subscribe.BlogSubscribe;
import com.mallang.blog.domain.subscribe.BlogSubscribeRepository;
import com.mallang.blog.domain.subscribe.BlogSubscribeValidator;
import com.mallang.blog.exception.AlreadySubscribedException;
import com.mallang.blog.exception.SelfSubscribeException;
import com.mallang.blog.exception.UnsubscribeUnsubscribedBlogException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("블로그 구독 서비스 (BlogSubscribeService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogSubscribeServiceTest {

    private final BlogRepository blogRepository = mock(BlogRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final BlogSubscribeRepository blogSubscribeRepository = mock(BlogSubscribeRepository.class);
    private final BlogSubscribeValidator blogSubscribeValidator = mock(BlogSubscribeValidator.class);
    private final BlogSubscribeService blogSubscribeService = new BlogSubscribeService(
            blogRepository,
            memberRepository,
            blogSubscribeRepository,
            blogSubscribeValidator
    );

    private final Long mallangId = 1L;
    private final Member mallang = 깃허브_말랑(mallangId);
    private final Member other = 깃허브_동훈(2L);
    private final Blog mallangBlog = mallangBlog(1L, mallang);
    private final Blog otherBlog = blog(2L, mallang);
    private final String otherBlogName = otherBlog.getName();

    @BeforeEach
    void setUp() {
        given(memberRepository.getById(mallang.getId())).willReturn(mallang);
        given(memberRepository.getById(other.getId())).willReturn(other);
        given(blogRepository.getByName(mallangBlog.getName())).willReturn(mallangBlog);
        given(blogRepository.getByName(otherBlog.getName())).willReturn(otherBlog);
    }

    @Nested
    class 블로그_구독_시 {

        @Test
        void 블로그를_구독한다() {
            // given
            BlogSubscribe blogSubscribe = new BlogSubscribe(mallang, otherBlog);
            given(blogSubscribeRepository.save(any()))
                    .willReturn(blogSubscribe);
            var command = new BlogSubscribeCommand(mallangId, otherBlogName);

            // when
            blogSubscribeService.subscribe(command);

            // then
            then(blogSubscribeRepository)
                    .should(times(1))
                    .save(any());
        }

        @Test
        void 자신의_블로그를_구독하면_예외() {
            // given
            willThrow(SelfSubscribeException.class)
                    .given(blogSubscribeValidator)
                    .validateSubscribe(any());
            var command = new BlogSubscribeCommand(mallangId, mallangBlog.getName());

            // when & then
            assertThatThrownBy(() ->
                    blogSubscribeService.subscribe(command)
            ).isInstanceOf(SelfSubscribeException.class);
        }

        @Test
        void 이미_구독한_블로그라면_예외() {
            // given
            willThrow(AlreadySubscribedException.class)
                    .given(blogSubscribeValidator)
                    .validateSubscribe(any());
            var command = new BlogSubscribeCommand(mallangId, otherBlogName);

            // when & then
            assertThatThrownBy(() ->
                    blogSubscribeService.subscribe(command)
            ).isInstanceOf(AlreadySubscribedException.class);
        }
    }

    @Nested
    class 블로그_구독_취소_시 {

        @Test
        void 구독한_블로그를_구독_취소한다() {
            // given
            BlogSubscribe blogSubscribe = new BlogSubscribe(mallang, otherBlog);
            given(blogSubscribeRepository.findBySubscriberAndBlog(mallangId, otherBlogName))
                    .willReturn(Optional.of(blogSubscribe));
            var unsubscribeCommand = new BlogUnsubscribeCommand(mallangId, otherBlogName);

            // when
            blogSubscribeService.unsubscribe(unsubscribeCommand);

            // then
            then(blogSubscribeRepository)
                    .should(times(1))
                    .delete(blogSubscribe);
        }

        @Test
        void 구독하지_않은_블로그라면_예외() {
            // given
            given(blogSubscribeRepository.findBySubscriberAndBlog(mallangId, otherBlogName))
                    .willReturn(Optional.empty());
            var command = new BlogUnsubscribeCommand(mallangId, otherBlogName);

            // when & then
            assertThatThrownBy(() ->
                    blogSubscribeService.unsubscribe(command)
            ).isInstanceOf(UnsubscribeUnsubscribedBlogException.class);
        }
    }
}
