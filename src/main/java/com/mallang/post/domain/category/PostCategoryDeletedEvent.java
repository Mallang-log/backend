package com.mallang.post.domain.category;

import com.mallang.common.domain.DomainEvent;

public record PostCategoryDeletedEvent(
        Long categoryId
) implements DomainEvent {

    @Override
    public Long id() {
        return categoryId();
    }
}
