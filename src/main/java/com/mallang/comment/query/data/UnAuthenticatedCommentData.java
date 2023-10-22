package com.mallang.comment.query.data;

import com.mallang.comment.domain.UnAuthenticatedComment;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class UnAuthenticatedCommentData extends CommentData {

    private WriterData writerData;
    private final String type = UNAUTHENTICATED_COMMENT_DATA_TYPE;

    @Builder
    public UnAuthenticatedCommentData(
            Long id,
            String content,
            LocalDateTime createdDate,
            boolean deleted,
            List<CommentData> children,
            WriterData writerData
    ) {
        super(id, content, createdDate, deleted, children);
        this.writerData = writerData;
    }

    public record WriterData(
            String nickname
    ) {
    }

    public static UnAuthenticatedCommentData from(UnAuthenticatedComment comment) {
        return UnAuthenticatedCommentData.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .deleted(comment.isDeleted())
                .children(comment.getChildren().stream()
                        .map(CommentData::from)
                        .toList())
                .writerData(new WriterData(comment.getNickname()))
                .build();
    }

}
