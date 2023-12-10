package com.mallang.category.domain.event;

import com.mallang.common.domain.DomainEvent;

public record PostCategoryDeletedEvent(
        Long categoryId
) implements DomainEvent {

    @Override
    public Long id() {
        return categoryId();
    }
}
