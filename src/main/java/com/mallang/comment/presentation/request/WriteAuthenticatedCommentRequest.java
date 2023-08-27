package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.WriteAuthenticatedCommentCommand;

public record WriteAuthenticatedCommentRequest(
        Long postId,
        String content
) {

    public WriteAuthenticatedCommentCommand toCommand(Long memberId) {
        return WriteAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .memberId(memberId)
                .build();
    }
}
