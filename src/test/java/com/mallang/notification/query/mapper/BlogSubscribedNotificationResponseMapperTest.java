package com.mallang.notification.query.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.type.BlogSubscribedNotification;
import com.mallang.notification.query.response.BlogSubscribedNotificationListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("블로그 구독 알림 응답 매퍼 (BlogSubscribedNotificationResponseMapper) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogSubscribedNotificationResponseMapperTest {

    private final BlogSubscribedNotificationResponseMapper mapper = new BlogSubscribedNotificationResponseMapper();

    @Test
    void 블로그_구독_알림을_지원한다() {
        // given
        var notification = mock(BlogSubscribedNotification.class);

        // when & then
        assertThat(mapper.support(notification)).isTrue();
    }

    @Test
    void 블로그_구독_알림이_아니면_지원하지_않는다() {
        // given
        var notification = mock(Notification.class);

        // when & then
        assertThat(mapper.support(notification)).isFalse();
    }

    @Test
    void 블로그_구독_알림을_응답_객체로_변환한다() {
        // given
        var notification = new BlogSubscribedNotification(1L, 2L, 3L);
        ReflectionTestUtils.setField(notification, "id", 4L);

        // when
        var response = (BlogSubscribedNotificationListResponse) mapper.mapToResponse(notification);

        // then
        assertThat(response.id()).isEqualTo(4L);
        assertThat(response.targetMemberId()).isEqualTo(1L);
        assertThat(response.blogId()).isEqualTo(2L);
        assertThat(response.subscriberId()).isEqualTo(3L);
    }
}
