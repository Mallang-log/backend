package com.mallang.comment.application.command;

import com.mallang.comment.domain.AnonymousWriter;
import com.mallang.comment.domain.CommentWriter;
import lombok.Builder;

public class WriteAnonymousCommentCommand extends WriteCommentCommand {

    private final String nickname;
    private final String password;

    @Builder
    public WriteAnonymousCommentCommand(
            Long postId,
            String content,
            boolean secret,
            String nickname,
            String password
    ) {
        super(postId, content, secret);
        this.nickname = nickname;
        this.password = password;
    }

    @Override
    protected CommentWriter commentWriter() {
        return new AnonymousWriter(nickname, password);
    }
}
