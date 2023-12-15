package com.mallang.blog.domain.subscribe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.auth.OauthMemberFixture;
import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.exception.AlreadySubscribedException;
import com.mallang.blog.exception.SelfSubscribeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("블로그 구독 (BlogSubscribe) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogSubscribeTest {

    private final BlogSubscribeValidator blogSubscribeValidator = mock(BlogSubscribeValidator.class);
    private final Member member = OauthMemberFixture.깃허브_말랑();
    private final Member other = OauthMemberFixture.깃허브_회원("other");
    private final Blog mallangBlog = new Blog("mallang-log", member);

    @Nested
    class 블로그_구독_시 {

        @Test
        void 자신의_블로그를_구독하는_경우_예외() {
            // given
            BlogSubscribe blogSubscribe = new BlogSubscribe(other, mallangBlog);
            willThrow(SelfSubscribeException.class)
                    .given(blogSubscribeValidator)
                    .validateSubscribe(blogSubscribe);

            // when & then
            assertThatThrownBy(() -> {
                blogSubscribe.subscribe(blogSubscribeValidator);
            }).isInstanceOf(SelfSubscribeException.class);
        }

        @Test
        void 이미_구독한_블로그를_다시_구독하면_예외() {
            // given
            BlogSubscribe blogSubscribe = new BlogSubscribe(other, mallangBlog);
            willThrow(AlreadySubscribedException.class)
                    .given(blogSubscribeValidator)
                    .validateSubscribe(blogSubscribe);

            // when & then
            assertThatThrownBy(() -> {
                blogSubscribe.subscribe(blogSubscribeValidator);
            }).isInstanceOf(AlreadySubscribedException.class);
        }

        @Test
        void 구독중이지_않은_타인의_블로그_구독시_성공() {
            // given
            BlogSubscribe blogSubscribe = new BlogSubscribe(other, mallangBlog);

            // when & then
            assertDoesNotThrow(() -> {
                blogSubscribe.subscribe(blogSubscribeValidator);
            });
        }

        @Test
        void 구독_이벤트_발행() {
            // given
            BlogSubscribe blogSubscribe = new BlogSubscribe(other, mallangBlog);

            // when
            blogSubscribe.subscribe(blogSubscribeValidator);

            // then
            assertThat(blogSubscribe.domainEvents())
                    .containsExactly(new BlogSubscribedEvent(blogSubscribe));
        }
    }
}
