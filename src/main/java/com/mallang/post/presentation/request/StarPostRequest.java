package com.mallang.post.presentation.request;

import com.mallang.post.application.command.StarPostCommand;
import jakarta.annotation.Nullable;

public record StarPostRequest(
        Long postId,
        String blogName
) {
    public StarPostCommand toCommand(Long memberId, @Nullable String postPassword) {
        return new StarPostCommand(postId, blogName, memberId, postPassword);
    }
}
