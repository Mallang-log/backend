package com.mallang.subscribe.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.common.ServiceTest;
import com.mallang.subscribe.application.command.BlogSubscribeCommand;
import com.mallang.subscribe.application.command.BlogUnsubscribeCommand;
import com.mallang.subscribe.exception.AlreadySubscribedException;
import com.mallang.subscribe.exception.SelfSubscribeException;
import com.mallang.subscribe.exception.UnsubscribeUnsubscribedBlogException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("블로그 구독 서비스 (BlogSubscribeService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogSubscribeServiceTest extends ServiceTest {

    private Long mallangId;
    private Long otherId;
    private String otherBlogName;

    @BeforeEach
    void setUp() {
        mallangId = 회원을_저장한다("mallang");
        otherId = 회원을_저장한다("other");
        otherBlogName = 블로그_개설(otherId, "other-log").getName();
    }

    @Nested
    class 블로그_구독_시 {

        @Test
        void 블로그를_구독한다() {
            // when
            Long subscribeId = blogSubscribeService.subscribe(new BlogSubscribeCommand(mallangId, otherBlogName));

            // then
            assertThat(blogSubscribeRepository.findById(subscribeId)).isPresent();
        }

        @Test
        void 자신의_블로그를_구독하면_예외() {
            // given
            String mallangBlogName = 블로그_개설(mallangId, "mallang-log").getName();

            // when & then
            assertThatThrownBy(() ->
                    blogSubscribeService.subscribe(new BlogSubscribeCommand(mallangId, mallangBlogName))
            ).isInstanceOf(SelfSubscribeException.class);
        }

        @Test
        void 이미_구독한_블로그라면_예외() {
            // given
            blogSubscribeService.subscribe(new BlogSubscribeCommand(mallangId, otherBlogName));

            // when & then
            assertThatThrownBy(() ->
                    blogSubscribeService.subscribe(new BlogSubscribeCommand(mallangId, otherBlogName))
            ).isInstanceOf(AlreadySubscribedException.class);
        }
    }

    @Nested
    class 블로그_구독_취소_시 {

        @Test
        void 구독한_블로그를_구독_취소한다() {
            // given
            Long subscribeId = blogSubscribeService.subscribe(new BlogSubscribeCommand(mallangId, otherBlogName));

            // when
            blogSubscribeService.unsubscribe(new BlogUnsubscribeCommand(mallangId, otherBlogName));

            // then
            assertThat(blogSubscribeRepository.findById(subscribeId)).isEmpty();
        }

        @Test
        void 구독하지_않은_블로그라면_예외() {
            // when & then
            assertThatThrownBy(() ->
                    blogSubscribeService.unsubscribe(new BlogUnsubscribeCommand(mallangId, otherBlogName))
            ).isInstanceOf(UnsubscribeUnsubscribedBlogException.class);
        }
    }
}
