package com.mallang.comment.application.command;

import lombok.Builder;

@Builder
public record DeleteAuthenticatedCommentCommand(
        Long memberId,
        Long commentId
) {
}
