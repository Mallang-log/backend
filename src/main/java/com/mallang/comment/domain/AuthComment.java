package com.mallang.comment.domain;

import static com.mallang.comment.domain.AuthComment.AUTH_COMMENT_TYPE;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.comment.exception.NoAuthorityCommentException;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
@DiscriminatorValue(AUTH_COMMENT_TYPE)
@Entity
public class AuthComment extends Comment {

    public static final String AUTH_COMMENT_TYPE = "AuthComment";

    private boolean secret;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @Builder
    public AuthComment(
            String content,
            Post post,
            @Nullable Comment parent,
            boolean secret,
            Member writer
    ) {
        super(content, post, parent);
        this.secret = secret;
        this.writer = writer;
    }

    @Override
    public void write(@Nullable String postPassword) {
        post.validateAccess(writer, postPassword);
    }

    public boolean canSee(Member member) {
        if (!secret) {
            return true;
        }
        if (post.isWriter(member)) {
            return true;
        }
        if (writer.equals(member)) {
            return true;
        }
        return parent != null && parent instanceof AuthComment authed && authed.getWriter().equals(member);
    }

    public void validateUpdate(Member member, @Nullable String postPassword) {
        post.validateAccess(member, postPassword);
        if (member.equals(writer)) {
            return;
        }
        throw new NoAuthorityCommentException();
    }

    public void update(
            String content,
            boolean secret
    ) {
        super.update(content);
        this.secret = secret;
    }

    public void validateDelete(Member member, @Nullable String postPassword) {
        post.validateAccess(member, postPassword);
        if (post.isWriter(member) || member.equals(writer)) {
            return;
        }
        throw new NoAuthorityCommentException();
    }
}
