package com.mallang.comment.application.command;

import com.mallang.comment.domain.AuthenticatedWriter;
import com.mallang.comment.domain.CommentWriter;
import lombok.Builder;

public class WriteAuthenticatedCommentCommand extends WriteCommentCommand {

    private final Long memberId;

    @Builder
    public WriteAuthenticatedCommentCommand(Long postId, String content, Long memberId) {
        super(postId, content);
        this.memberId = memberId;
    }

    @Override
    protected CommentWriter commentWriter() {
        return new AuthenticatedWriter(memberId);
    }
}
