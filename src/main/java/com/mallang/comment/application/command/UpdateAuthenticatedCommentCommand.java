package com.mallang.comment.application.command;

public record UpdateAuthenticatedCommentCommand(
        Long commentId,
        String content,
        boolean secret,
        Long memberId
) {
}
