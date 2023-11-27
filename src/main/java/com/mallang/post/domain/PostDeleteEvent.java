package com.mallang.post.domain;

import com.mallang.common.domain.DomainEvent;
import java.time.LocalDateTime;

public record PostDeleteEvent(
        PostId postId,
        LocalDateTime deletedDate
) implements DomainEvent {

    public PostDeleteEvent(PostId postId) {
        this(postId, LocalDateTime.now());
    }

    @Override
    public PostId id() {
        return postId();
    }
}
