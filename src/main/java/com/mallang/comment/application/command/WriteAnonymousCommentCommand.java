package com.mallang.comment.application.command;

import com.mallang.comment.domain.AnonymousWriter;
import com.mallang.comment.domain.Comment;
import com.mallang.post.domain.Post;
import lombok.Builder;

@Builder
public record WriteAnonymousCommentCommand(
        Long postId,
        String content,
        String nickname,
        String password
) {
    public Comment toComment(Post post) {
        return Comment.builder()
                .post(post)
                .content(content)
                .commentWriter(new AnonymousWriter(nickname, password))
                .secret(false)
                .build();
    }
}
