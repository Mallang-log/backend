package com.mallang.subscribe.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.blog.domain.Blog;
import com.mallang.member.MemberFixture;
import com.mallang.member.domain.Member;
import com.mallang.subscribe.exception.AlreadySubscribedException;
import com.mallang.subscribe.exception.SelfSubscribeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("블로그 구독 검증기(BlogSubscribeValidator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogSubscribeValidatorTest {

    private final BlogSubscribeRepository blogSubscribeRepository = mock(BlogSubscribeRepository.class);
    private final BlogSubscribeValidator blogSubscribeValidator = new BlogSubscribeValidator(blogSubscribeRepository);
    private final Member member = MemberFixture.말랑();
    private final Member other = MemberFixture.회원("other");
    private final Blog mallangBlog = new Blog("mallang-log", member);

    @Nested
    class 블로그_구독_시 {

        @Test
        void 자신의_블로그를_구독하는_경우_예외() {
            // given
            BlogSubscribe blogSubscribe = new BlogSubscribe(member, mallangBlog);

            // when & then
            assertThatThrownBy(() -> {
                blogSubscribeValidator.validateSubscribe(blogSubscribe);
            }).isInstanceOf(SelfSubscribeException.class);
        }

        @Test
        void 이미_구독한_블로그를_다시_구독하면_예외() {
            // given
            BlogSubscribe blogSubscribe = new BlogSubscribe(other, mallangBlog);
            given(blogSubscribeRepository.existsBySubscriberAndBlog(any(), any()))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> {
                blogSubscribeValidator.validateSubscribe(blogSubscribe);
            }).isInstanceOf(AlreadySubscribedException.class);
        }

        @Test
        void 구독중이지_않은_타인의_블로그_구독시_성공() {
            // given
            BlogSubscribe blogSubscribe = new BlogSubscribe(other, mallangBlog);
            given(blogSubscribeRepository.existsBySubscriberAndBlog(any(), any()))
                    .willReturn(false);

            // when & then
            assertDoesNotThrow(() -> {
                blogSubscribeValidator.validateSubscribe(blogSubscribe);
            });
        }
    }
}
