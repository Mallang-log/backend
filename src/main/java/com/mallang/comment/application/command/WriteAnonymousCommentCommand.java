package com.mallang.comment.application.command;

import com.mallang.comment.domain.AnonymousWriter;
import com.mallang.comment.domain.CommentWriter;

public class WriteAnonymousCommentCommand extends WriteCommentCommand {

    private final String nickname;
    private final String password;

    public WriteAnonymousCommentCommand(
            Long postId,
            String content,
            String nickname,
            String password
    ) {
        super(postId, content);
        this.nickname = nickname;
        this.password = password;
    }

    @Override
    protected CommentWriter commentWriter() {
        return new AnonymousWriter(nickname, password);
    }
}
