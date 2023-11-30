package com.mallang.comment.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.comment.exception.NoAuthorityCommentException;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class AuthComment extends Comment {

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
