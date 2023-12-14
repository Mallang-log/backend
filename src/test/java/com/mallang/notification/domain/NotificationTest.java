package com.mallang.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("알림 (Notification) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class NotificationTest {

    static class TestNotification extends Notification {
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
}
