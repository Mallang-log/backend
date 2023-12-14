package com.mallang.notification.domain.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.common.domain.DomainEvent;
import com.mallang.notification.domain.Notification;
import com.mallang.notification.exception.NotificationConvertException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("복합 알림 생성기 (NotificationGeneratorComposite) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class NotificationGeneratorCompositeTest {

    @Test
    void 자신이_가진_알림_생성기들_중_주어진_이벤트를_받아_알림을_생성할_수_있는_생성기가_있으면_변환한다() {
        // given
        var converter1 = mock(NotificationGenerator.class);
        var converter2 = mock(NotificationGenerator.class);
        var composite = new NotificationGeneratorComposite(List.of(converter1, converter2));
        var mockEvent = mock(DomainEvent.class);
        var mockNotification = mock(Notification.class);
        given(converter1.canGenerateFrom(mockEvent))
                .willReturn(false);
        given(converter2.canGenerateFrom(mockEvent))
                .willReturn(true);
        given(converter2.generate(mockEvent))
                .willReturn(List.of(mockNotification));

        // when
        Notification result = composite.generate(mockEvent).get(0);

        // then
        assertThat(result).isEqualTo(mockNotification);
    }

    @Test
    void 가진_알림_생성기_중_주어진_이벤트를_처리할_수_있는_것이_없다면_예외() {
        // given
        var converter1 = mock(NotificationGenerator.class);
        var converter2 = mock(NotificationGenerator.class);
        var composite = new NotificationGeneratorComposite(List.of(converter1, converter2));
        var mockEvent = mock(DomainEvent.class);
        var mockNotification = mock(Notification.class);
        given(converter1.canGenerateFrom(mockEvent))
                .willReturn(false);
        given(converter2.canGenerateFrom(mockEvent))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> {
            composite.generate(mockEvent);
        }).isInstanceOf(NotificationConvertException.class);
    }
}
