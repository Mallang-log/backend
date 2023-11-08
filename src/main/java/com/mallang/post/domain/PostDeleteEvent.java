package com.mallang.post.domain;

import java.time.LocalDateTime;

public record PostDeleteEvent(
        Long postId,
        Long blogId,
        LocalDateTime deletedDate
) {

    public PostDeleteEvent(Long postId, Long blogId) {
        this(postId, blogId, LocalDateTime.now());
    }
}
