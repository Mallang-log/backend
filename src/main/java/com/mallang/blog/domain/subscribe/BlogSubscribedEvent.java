package com.mallang.blog.domain.subscribe;

import com.mallang.common.domain.DomainEvent;

public record BlogSubscribedEvent(
        BlogSubscribe blogSubscribe
) implements DomainEvent<Long> {

    @Override
    public Long id() {
        return blogSubscribe.getId();
    }
}
