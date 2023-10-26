package com.mallang.comment.query.data;

import com.mallang.comment.domain.AuthenticatedComment;
import com.mallang.member.domain.Member;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthenticatedCommentData extends CommentData {

    private WriterData writerData;
    private boolean secret;
    private final String type = AUTHENTICATED_COMMENT_DATA_TYPE;

    @Builder
    public AuthenticatedCommentData(
            Long id,
            String content,
            LocalDateTime createdDate,
            boolean deleted,
            List<CommentData> children,
            WriterData writerData,
            boolean secret
    ) {
        super(id, content, createdDate, deleted, children);
        this.writerData = writerData;
        this.secret = secret;
    }

    public record WriterData(
            Long memberId,
            String nickname,
            String profileImageUrl
    ) {

        public static WriterData ANONYMOUS = new WriterData(null, "익명", null);

        public static WriterData from(Member writer) {
            return new WriterData(writer.getId(), writer.getNickname(), writer.getProfileImageUrl());
        }
    }

    public static AuthenticatedCommentData from(AuthenticatedComment comment) {
        return AuthenticatedCommentData.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .deleted(comment.isDeleted())
                .children(comment.getChildren().stream()
                        .map(CommentData::from)
                        .toList())
                .writerData(WriterData.from(comment.getWriter()))
                .secret(comment.isSecret())
                .build();
    }
}
