package com.mallang.comment.query.response;

import com.mallang.comment.domain.UnAuthComment;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class UnAuthCommentResponse extends CommentResponse {

    private final WriterResponse writer;
    private final String type = UN_AUTH_COMMENT_DATA_TYPE;

    @Builder
    public UnAuthCommentResponse(
            Long id,
            String content,
            LocalDateTime createdDate,
            boolean deleted,
            WriterResponse writer
    ) {
        super(id, content, createdDate, deleted);
        this.writer = writer;
    }

    public static UnAuthCommentResponse from(UnAuthComment comment) {
        return UnAuthCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .deleted(comment.isDeleted())
                .writer(new WriterResponse(comment.getNickname()))
                .build();
    }

    public record WriterResponse(
            String nickname
    ) {
    }
}
