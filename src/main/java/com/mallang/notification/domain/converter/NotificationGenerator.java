package com.mallang.notification.domain.converter;

import com.mallang.common.domain.DomainEvent;
import com.mallang.notification.domain.Notification;
import java.util.List;

public interface NotificationGenerator {

    boolean canGenerateFrom(DomainEvent<?> domainEvent);

    List<Notification> generate(DomainEvent<?> domainEvent);
}
