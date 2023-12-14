package com.mallang.notification.domain.converter;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.domain.BlogFixture.mallangBlog;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.subscribe.BlogSubscribe;
import com.mallang.blog.domain.subscribe.BlogSubscribeRepository;
import com.mallang.blog.domain.subscribe.BlogSubscribedEvent;
import com.mallang.common.domain.DomainEvent;
import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.type.BlogSubscribedNotification;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("블로그 구독 알림 생성기 (BlogSubscribedNotificationGenerator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogSubscribedNotificationGeneratorTest {

    private final BlogSubscribeRepository repository = mock(BlogSubscribeRepository.class);
    private final BlogSubscribedNotificationGenerator converter = new BlogSubscribedNotificationGenerator(repository);
    private final Member mallang = 깃허브_말랑(1L);
    private final Member donghun = 깃허브_동훈(2L);
    private final Blog blog = mallangBlog(3L, mallang);
    private final BlogSubscribe blogSubscribe = new BlogSubscribe(donghun, blog);

    @Test
    void 블로그_구독_이벤트를_통해_구독_알림을_생성할_수_있다() {
        // given
        BlogSubscribedEvent event = new BlogSubscribedEvent(blogSubscribe);

        // when & then
        assertThat(converter.canGenerateFrom(event)).isTrue();
    }

    @Test
    void 블로그_구독_이벤트가_아니면_구독_알림을_생성할_수_없다() {
        // given
        DomainEvent<?> event = mock(DomainEvent.class);

        // when & then
        assertThat(converter.canGenerateFrom(event)).isFalse();

    }

    @Test
    void 블로그_구독_이벤트를_통해_구독_알림을_생성한다() {
        // given
        ReflectionTestUtils.setField(blogSubscribe, "id", 1L);
        BlogSubscribedEvent event = new BlogSubscribedEvent(blogSubscribe);
        given(repository.getById(1L)).willReturn(blogSubscribe);

        // when
        List<Notification> generate = converter.generate(event);

        // then
        BlogSubscribedNotification notification = (BlogSubscribedNotification) generate.get(0);
        assertThat(notification.getTargetMemberId()).isEqualTo(1L);
        assertThat(notification.getBlogId()).isEqualTo(3L);
        assertThat(notification.getSubscriberId()).isEqualTo(2L);
    }
}
