package com.mallang.comment.domain;

import com.mallang.common.domain.DomainEvent;

public record CommentWrittenEvent(
        Comment comment
) implements DomainEvent<Long> {

    @Override
    public Long id() {
        return comment.getId();
    }
}
