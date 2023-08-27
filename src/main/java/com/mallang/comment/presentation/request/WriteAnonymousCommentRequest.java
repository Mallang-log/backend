package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.WriteAnonymousCommentCommand;
import lombok.Builder;

@Builder
public record WriteAnonymousCommentRequest(
        Long postId,
        String content,
        String nickname,
        String password
) {

    public WriteAnonymousCommentCommand toCommand() {
        return WriteAnonymousCommentCommand.builder()
                .postId(postId)
                .content(content)
                .nickname(nickname)
                .password(password)
                .secret(false)
                .build();
    }
}
