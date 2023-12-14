package com.mallang.notification.application;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.common.domain.DomainEvent;
import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.NotificationRepository;
import com.mallang.notification.domain.converter.NotificationGeneratorComposite;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("알림 이벤트 핸들러 (NotificationEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class NotificationEventHandlerTest {

    private final NotificationGeneratorComposite composite = mock(NotificationGeneratorComposite.class);
    private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
    private final NotificationEventHandler notificationEventHandler =
            new NotificationEventHandler(composite, notificationRepository);

    @Test
    void 특정_도메인_이벤트를_받아_알림으로_변환한다() {
        // given
        var event = mock(DomainEvent.class);
        var notification = mock(Notification.class);
        given(composite.generate(event)).willReturn(List.of(notification));

        // when
        notificationEventHandler.createFrom(event);

        // then
        then(notificationRepository)
                .should(times(1))
                .saveAll(List.of(notification));
    }
}
