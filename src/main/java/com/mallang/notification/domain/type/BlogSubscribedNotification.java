package com.mallang.notification.domain.type;

import static com.mallang.notification.domain.type.BlogSubscribedNotification.BLOG_SUBSCRIBED_NOTIFICATION_TYPE;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.notification.domain.Notification;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
@DiscriminatorValue(BLOG_SUBSCRIBED_NOTIFICATION_TYPE)
@Entity
public class BlogSubscribedNotification extends Notification {

    public static final String BLOG_SUBSCRIBED_NOTIFICATION_TYPE = "BlogSubscribed";

    private Long blogId;
    private Long subscriberId;

    public BlogSubscribedNotification(Long targetMemberId, Long blogId, Long subscriberId) {
        super(targetMemberId);
        this.blogId = blogId;
        this.subscriberId = subscriberId;
    }
}
