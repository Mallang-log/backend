package com.mallang.notification.domain.type;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.notification.domain.Notification;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("BlogSubscribed")
@Entity
public class BlogSubscribedNotification extends Notification {

    private Long blogId;
    private Long subscriberId;

    public BlogSubscribedNotification(Long targetMemberId, Long blogId, Long subscriberId) {
        super(targetMemberId);
        this.blogId = blogId;
        this.subscriberId = subscriberId;
    }
}
