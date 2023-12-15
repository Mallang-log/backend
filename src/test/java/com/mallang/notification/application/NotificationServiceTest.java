package com.mallang.notification.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.NotificationRepository;
import com.mallang.notification.domain.type.BlogSubscribedNotification;
import com.mallang.notification.exception.NoAuthorityNotificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("알림 서비스 (NotificationService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class NotificationServiceTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
    private final NotificationService notificationService = new NotificationService(
            memberRepository,
            notificationRepository
    );

    private final Member member = 깃허브_말랑(1L);
    private final Member other = 깃허브_동훈(2L);
    private final Notification notification = new BlogSubscribedNotification(1L, 1L, 1L);

    @BeforeEach
    void setUp() {
        given(memberRepository.getById(member.getId()))
                .willReturn(member);
        given(memberRepository.getById(other.getId()))
                .willReturn(other);
        ReflectionTestUtils.setField(notification, "id", 2L);
        given(notificationRepository.getById(2L))
                .willReturn(notification);
    }

    @Nested
    class 알림_읽음_처리_시 {

        @Test
        void 알림을_읽음처리한다() {
            // when
            notificationService.read(1L, 2L);

            // then
            assertThat(notification.isRead()).isTrue();
        }

        @Test
        void 알림에_대한_권한이_없으면_예외() {
            // when
            assertThatThrownBy(() -> {
                notificationService.read(2L, 2L);
            }).isInstanceOf(NoAuthorityNotificationException.class);

            // then
            assertThat(notification.isRead()).isFalse();
        }
    }

    @Nested
    class 알림_제거_시 {

        @Test
        void 알림을_제거힌다() {
            // when
            notificationService.delete(1L, 2L);

            // then
            then(notificationRepository)
                    .should(times(1))
                    .delete(notification);
        }

        @Test
        void 알림에_대한_권한이_없으면_예외() {
            // when
            assertThatThrownBy(() -> {
                notificationService.delete(2L, 2L);
            }).isInstanceOf(NoAuthorityNotificationException.class);

            // then
            then(notificationRepository)
                    .should(times(0))
                    .delete(notification);
        }
    }
}
