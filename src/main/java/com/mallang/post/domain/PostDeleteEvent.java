package com.mallang.post.domain;

import com.mallang.common.domain.DomainEvent;
import java.time.LocalDateTime;

public record PostDeleteEvent(
        Long postId,
        LocalDateTime deletedDate
) implements DomainEvent {

    public PostDeleteEvent(Long postId) {
        this(postId, LocalDateTime.now());
    }

    @Override
    public Long id() {
        return postId();
    }
}
