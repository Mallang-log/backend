package com.mallang.comment.application.command;

import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.writer.UnAuthenticatedWriter;
import com.mallang.post.domain.Post;
import lombok.Builder;

@Builder
public record WriteUnAuthenticatedCommentCommand(
        Long postId,
        String content,
        String nickname,
        String password
) {
    public Comment toComment(Post post) {
        return Comment.builder()
                .post(post)
                .content(content)
                .commentWriter(new UnAuthenticatedWriter(nickname, password))
                .secret(false)
                .build();
    }
}
