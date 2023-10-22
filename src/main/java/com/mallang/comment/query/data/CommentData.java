package com.mallang.comment.query.data;

import com.mallang.comment.domain.Comment;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record CommentData(
        Long id,
        String content,
        boolean secret,
        CommentWriterData commentWriterData,
        LocalDateTime createdDate,
        boolean deleted,
        List<CommentData> children
) {

    public static CommentData from(Comment comment) {
        return CommentData.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .secret(comment.isSecret())
                .commentWriterData(CommentWriterData.from(comment.getCommentWriter()))
                .createdDate(comment.getCreatedDate())
                .deleted(comment.isDeleted())
                .children(comment.getChildren().stream()
                        .map(CommentData::from)
                        .toList())
                .build();
    }
}
