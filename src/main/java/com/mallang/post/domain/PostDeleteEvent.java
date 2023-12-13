package com.mallang.post.domain;

import com.mallang.common.domain.DomainEvent;

public record PostDeleteEvent(
        PostId postId
) implements DomainEvent<PostId> {

    @Override
    public PostId id() {
        return postId();
    }
}
