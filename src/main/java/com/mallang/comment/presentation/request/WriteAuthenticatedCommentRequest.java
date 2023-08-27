package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.WriteAuthenticatedCommentCommand;

public record WriteAuthenticatedCommentRequest(
        Long postId,
        String content,
        boolean secret
) {

    public WriteAuthenticatedCommentCommand toCommand(Long memberId) {
        return WriteAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .secret(secret)
                .memberId(memberId)
                .build();
    }
}
