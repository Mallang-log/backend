package com.mallang.comment.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.exception.NoAuthorityForCommentException;
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
public class AuthenticatedComment extends Comment {

    private boolean secret;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @Builder
    public AuthenticatedComment(
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
        post.validatePostAccessibility(writer, postPassword);
    }

    public void update(
            Member writer,
            String content,
            boolean secret,
            @Nullable String postPassword
    ) {
        post.validatePostAccessibility(writer, postPassword);
        validateWriter(writer);
        super.update(content);
        this.secret = secret;
    }

    public void delete(Member member, CommentDeleteService commentDeleteService, @Nullable String postPassword) {
        post.validatePostAccessibility(member, postPassword);
        if (!isPostOwner(member)) {
            validateWriter(member);
        }
        super.delete(commentDeleteService);
    }

    private void validateWriter(Member writer) {
        if (!writer.equals(getWriter())) {
            throw new NoAuthorityForCommentException();
        }
    }
}
