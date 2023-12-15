package com.mallang.notification.domain;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.auth.domain.Member;
import com.mallang.notification.exception.NoAuthorityNotificationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("알림 (Notification) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class NotificationTest {

    static class TestNotification extends Notification {

        public TestNotification() {
        }

        public TestNotification(Long targetMemberId) {
            super(targetMemberId);
        }
    }

    @Test
    void 생성_시_안읽음_상태이다() {
        // when
        TestNotification testNotification = new TestNotification();

        // then
        assertThat(testNotification.isRead()).isFalse();
    }

    @Test
    void 읽을_수_있다() {
        // given
        TestNotification testNotification = new TestNotification();

        // when
        testNotification.read();

        // then
        assertThat(testNotification.isRead()).isTrue();
    }

    @Test
    void 주인을_검증한다() {
        // given
        TestNotification testNotification = new TestNotification(1L);
        Member member = 깃허브_동훈(1L);

        // when & then
        assertDoesNotThrow(() -> {
            testNotification.validateMember(member);
        });
    }

    @Test
    void 주인_검증_실패_시_예외() {
        // given
        TestNotification testNotification = new TestNotification(1L);
        Member member = 깃허브_동훈(2L);

        // when & then
        assertThatThrownBy(() -> {
            testNotification.validateMember(member);
        }).isInstanceOf(NoAuthorityNotificationException.class);
    }
}
