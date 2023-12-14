package com.mallang.notification.query.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.notification.domain.Notification;
import com.mallang.notification.exception.NotificationResponseMappingException;
import com.mallang.notification.query.response.NotificationListResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("알림 응답 복합 매퍼 (NotificationResponseMapperComposite) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class NotificationResponseMapperCompositeTest {

    @Test
    void 자신이_가진_알림_응답_매퍼들_중_주어진_알림을_지원하는_매퍼가_있으면_이를_사용한다() {
        // given
        var mapper1 = mock(NotificationResponseMapper.class);
        var mapper2 = mock(NotificationResponseMapper.class);
        var composite = new NotificationResponseMapperComposite(List.of(mapper1, mapper2));
        var mockNotification = mock(Notification.class);
        var mockResponse = mock(NotificationListResponse.class);
        given(mapper1.support(mockNotification))
                .willReturn(false);
        given(mapper2.support(mockNotification))
                .willReturn(true);
        given(mapper2.mapToResponse(mockNotification))
                .willReturn(mockResponse);

        // when
        NotificationListResponse result = composite.mapToResponse(mockNotification);

        // then
        assertThat(result).isEqualTo(mockResponse);
    }

    @Test
    void 자신이_가진_알림_응답_매퍼들_중_주어진_알림을_지원하는_매퍼가_없으면_예외() {
        // given
        var mapper1 = mock(NotificationResponseMapper.class);
        var mapper2 = mock(NotificationResponseMapper.class);
        var composite = new NotificationResponseMapperComposite(List.of(mapper1, mapper2));
        var mockNotification = mock(Notification.class);
        given(mapper1.support(mockNotification))
                .willReturn(false);
        given(mapper2.support(mockNotification))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> {
            composite.mapToResponse(mockNotification);
        }).isInstanceOf(NotificationResponseMappingException.class);
    }
}
