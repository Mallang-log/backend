package com.mallang.category.domain.event;

import com.mallang.common.domain.DomainEvent;

public record CategoryDeletedEvent(
        Long categoryId
) implements DomainEvent {

    @Override
    public Long id() {
        return categoryId();
    }
}
