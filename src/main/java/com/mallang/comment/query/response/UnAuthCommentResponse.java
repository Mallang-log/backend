package com.mallang.comment.query.response;

import com.mallang.comment.domain.UnAuthenticatedComment;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class UnAuthCommentResponse extends CommentResponse {

    private final WriterResponse writer;
    private final String type = UNAUTHENTICATED_COMMENT_DATA_TYPE;

    @Builder
    public UnAuthCommentResponse(
            Long id,
            String content,
            LocalDateTime createdDate,
            boolean deleted,
            List<CommentResponse> children,
            WriterResponse writer
    ) {
        super(id, content, createdDate, deleted, children);
        this.writer = writer;
    }

    public static UnAuthCommentResponse from(UnAuthenticatedComment comment) {
        return UnAuthCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .deleted(comment.isDeleted())
                .children(comment.getChildren().stream()
                        .map(CommentResponse::from)
                        .toList())
                .writer(new WriterResponse(comment.getNickname()))
                .build();
    }

    public record WriterResponse(
            String nickname
    ) {
    }
}
