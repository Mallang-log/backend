package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.WriteAuthCommentCommand;
import jakarta.annotation.Nullable;

public record WriteAuthCommentRequest(
        Long postId,
        String content,
        boolean secret,
        @Nullable Long parentCommentId
) {

    public WriteAuthCommentCommand toCommand(Long memberId, @Nullable String postPassword) {
        return WriteAuthCommentCommand.builder()
                .postId(postId)
                .postPassword(postPassword)
                .content(content)
                .secret(secret)
                .memberId(memberId)
                .parentCommentId(parentCommentId)
                .build();
    }
}
