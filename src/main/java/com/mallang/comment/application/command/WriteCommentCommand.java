package com.mallang.comment.application.command;

import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentWriter;
import com.mallang.post.domain.Post;

public abstract class WriteCommentCommand {

    protected final Long postId;
    protected final String content;
    protected final boolean secret;

    protected WriteCommentCommand(Long postId, String content, boolean secret) {
        this.postId = postId;
        this.content = content;
        this.secret = secret;
    }

    public Comment toComment(Post post) {
        return Comment.builder()
                .post(post)
                .content(content)
                .commentWriter(commentWriter())
                .secret(secret)
                .build();
    }

    protected abstract CommentWriter commentWriter();

    public Long postId() {
        return postId;
    }
}
