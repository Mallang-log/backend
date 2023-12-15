package com.mallang.notification.domain.generator;

import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.subscribe.BlogSubscribe;
import com.mallang.blog.domain.subscribe.BlogSubscribeRepository;
import com.mallang.blog.domain.subscribe.BlogSubscribedEvent;
import com.mallang.common.domain.DomainEvent;
import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.type.BlogSubscribedNotification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BlogSubscribedNotificationGenerator implements NotificationGenerator {

    private final BlogSubscribeRepository blogSubscribeRepository;

    @Override
    public boolean canGenerateFrom(DomainEvent<?> domainEvent) {
        return domainEvent instanceof BlogSubscribedEvent;
    }

    @Override
    public List<Notification> generate(DomainEvent<?> domainEvent) {
        BlogSubscribedEvent event = (BlogSubscribedEvent) domainEvent;
        BlogSubscribe blogSubscribe = blogSubscribeRepository.getById(event.id());
        Blog blog = blogSubscribe.getBlog();
        return List.of(new BlogSubscribedNotification(
                blog.getOwner().getId(),
                blog.getId(),
                blogSubscribe.getSubscriber().getId()
        ));
    }
}
