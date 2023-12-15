package com.mallang.notification.query;

import static com.mallang.notification.domain.type.BlogSubscribedNotification.BLOG_SUBSCRIBED_NOTIFICATION_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.type.BlogSubscribedNotification;
import com.mallang.notification.query.mapper.NotificationResponseMapperComposite;
import com.mallang.notification.query.repository.NotificationQueryRepository;
import com.mallang.notification.query.response.BlogSubscribedNotificationListResponse;
import com.mallang.notification.query.response.NotificationListResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@DisplayName("알림 조회 서비스 (NotificationQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class NotificationQueryServiceTest {

    private final NotificationQueryRepository notificationQueryRepository = mock(NotificationQueryRepository.class);
    private final NotificationResponseMapperComposite composite = mock(NotificationResponseMapperComposite.class);
    private final NotificationQueryService notificationQueryService = new NotificationQueryService(
            notificationQueryRepository,
            composite
    );

    @Test
    void 특정_회원의_모든_알림을_조회한다() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 30);
        var notification = new BlogSubscribedNotification(1L, 2L, 3L);
        PageImpl<Notification> result = new PageImpl<>(
                List.of(notification),
                pageRequest,
                50
        );
        given(notificationQueryRepository.findAllByTargetMemberIdOrderByCreatedDateDesc(1L, pageRequest))
                .willReturn(result);
        given(composite.mapToResponse(notification))
                .willReturn(new BlogSubscribedNotificationListResponse(
                        BLOG_SUBSCRIBED_NOTIFICATION_TYPE,
                        1L,
                        false,
                        1L,
                        2L,
                        3L
                ));

        // when
        Page<NotificationListResponse> response =
                notificationQueryService.findAllByMemberId(1L, pageRequest);

        // then
        assertThat(response.getContent())
                .hasSize(1)
                .containsExactly(new BlogSubscribedNotificationListResponse(
                        BLOG_SUBSCRIBED_NOTIFICATION_TYPE,
                        1L,
                        false,
                        1L,
                        2L,
                        3L
                ));
    }
}
