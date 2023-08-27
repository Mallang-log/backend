package com.mallang.comment.application.command;

import com.mallang.comment.domain.AuthenticatedWriter;
import com.mallang.comment.domain.CommentWriter;

public class WriteAuthenticatedCommentCommand extends WriteCommentCommand {

    private final Long memberId;

    public WriteAuthenticatedCommentCommand(Long postId, String content, Long memberId) {
        super(postId, content);
        this.memberId = memberId;
    }

    @Override
    protected CommentWriter commentWriter() {
        return new AuthenticatedWriter(memberId);
    }
}
