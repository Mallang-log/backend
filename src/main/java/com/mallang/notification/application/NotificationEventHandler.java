package com.mallang.notification.application;

import com.mallang.blog.domain.subscribe.BlogSubscribedEvent;
import com.mallang.comment.domain.CommentWrittenEvent;
import com.mallang.common.domain.DomainEvent;
import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.NotificationRepository;
import com.mallang.notification.domain.generator.NotificationGeneratorComposite;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationEventHandler {

    private final NotificationGeneratorComposite notificationGeneratorComposite;
    private final NotificationRepository notificationRepository;

    @EventListener({
            CommentWrittenEvent.class,
            BlogSubscribedEvent.class
    })
    public void createFrom(DomainEvent<?> event) {
        List<Notification> notifications = notificationGeneratorComposite.generate(event);
        notificationRepository.saveAll(notifications);
    }
}
