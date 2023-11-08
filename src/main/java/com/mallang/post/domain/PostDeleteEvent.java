package com.mallang.post.domain;

import java.time.LocalDateTime;

public record PostDeleteEvent(
        Long postId,
        LocalDateTime deletedDate
) {

    public PostDeleteEvent(Long postId) {
        this(postId, LocalDateTime.now());
    }
}
