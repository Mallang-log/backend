package com.mallang.comment.application.command;

public record UpdateAnonymousCommentCommand(
        Long commentId,
        String content,
        Long commentWriterId,
        String nickname,
        String password
) {
}
