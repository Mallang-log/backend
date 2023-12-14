package com.mallang.notification.domain.converter;

import com.mallang.common.domain.DomainEvent;
import com.mallang.notification.domain.Notification;
import com.mallang.notification.exception.NotificationConvertException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationGeneratorComposite {

    private final List<NotificationGenerator> converters;

    public List<Notification> generate(DomainEvent<?> event) {
        for (NotificationGenerator converter : converters) {
            if (converter.canGenerateFrom(event)) {
                return converter.generate(event);
            }
        }
        throw new NotificationConvertException(event);
    }
}
