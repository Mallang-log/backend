package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.WriteUnAuthenticatedCommentCommand;
import lombok.Builder;

@Builder
public record WriteAnonymousCommentRequest(
        Long postId,
        String content,
        String nickname,
        String password
) {

    public WriteUnAuthenticatedCommentCommand toCommand() {
        return WriteUnAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .nickname(nickname)
                .password(password)
                .build();
    }
}
