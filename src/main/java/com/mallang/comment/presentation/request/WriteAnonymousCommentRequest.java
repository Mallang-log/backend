package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.WriteUnAuthenticatedCommentCommand;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record WriteAnonymousCommentRequest(
        Long postId,
        String content,
        String nickname,
        String password,
        @Nullable Long parentCommentId
) {

    public WriteUnAuthenticatedCommentCommand toCommand() {
        return WriteUnAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .nickname(nickname)
                .password(password)
                .parentCommentId(parentCommentId)
                .build();
    }
}
