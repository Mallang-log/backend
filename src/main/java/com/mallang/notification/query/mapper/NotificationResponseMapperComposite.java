package com.mallang.notification.query.mapper;

import com.mallang.notification.domain.Notification;
import com.mallang.notification.exception.NotificationResponseMappingException;
import com.mallang.notification.query.response.NotificationListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationResponseMapperComposite {

    private final List<NotificationResponseMapper> notificationResponseMappers;

    public NotificationListResponse mapToResponse(Notification notification) {
        for (NotificationResponseMapper mapper : notificationResponseMappers) {
            if (mapper.support(notification)) {
                return mapper.mapToResponse(notification);
            }
        }
        throw new NotificationResponseMappingException(notification);
    }
}
