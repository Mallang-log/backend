package com.mallang.comment.query.response;

import static com.mallang.comment.domain.AuthComment.AUTH_COMMENT_TYPE;
import static com.mallang.comment.query.response.AuthCommentResponse.WriterResponse.ANONYMOUS;

import com.mallang.auth.domain.Member;
import com.mallang.comment.domain.AuthComment;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class AuthCommentResponse extends CommentResponse {

    private final WriterResponse writer;
    private final boolean secret;
    private final String type = AUTH_COMMENT_TYPE;

    @Builder
    public AuthCommentResponse(
            Long id,
            String content,
            LocalDateTime createdDate,
            boolean deleted,
            WriterResponse writer,
            boolean secret
    ) {
        super(id, content, createdDate, deleted);
        this.writer = writer;
        this.secret = secret;
    }

    public static AuthCommentResponse from(AuthComment comment) {
        return AuthCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .deleted(comment.isDeleted())
                .writer(WriterResponse.from(comment.getWriter()))
                .secret(comment.isSecret())
                .build();
    }

    public static AuthCommentResponse protectFrom(AuthComment comment) {
        return AuthCommentResponse.builder()
                .id(comment.getId())
                .content("비밀 댓글입니다.")
                .createdDate(comment.getCreatedDate())
                .deleted(comment.isDeleted())
                .writer(ANONYMOUS)
                .secret(true)
                .build();
    }

    public record WriterResponse(
            Long memberId,
            String nickname,
            String profileImageUrl
    ) {
        public static WriterResponse ANONYMOUS = new WriterResponse(null, "익명", null);

        public static WriterResponse from(Member writer) {
            return new WriterResponse(writer.getId(), writer.getNickname(), writer.getProfileImageUrl());
        }
    }
}
