package com.mallang.comment.domain;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.comment.exception.CannotWriteSecretCommentException;
import com.mallang.comment.exception.NoAuthorityForCommentException;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.post.domain.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Comment extends CommonDomainModel {

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = LAZY, cascade = {PERSIST, REMOVE})
    @JoinColumn(name = "comment_writer_id", nullable = false)
    private CommentWriter commentWriter;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private boolean secret;

    @Builder
    public Comment(String content, CommentWriter commentWriter, Post post, boolean secret) {
        this.content = content;
        this.commentWriter = commentWriter;
        this.post = post;
        setSecret(secret);
    }

    public void setSecret(boolean secret) {
        validateSecret(secret);
        this.secret = secret;
    }

    private void validateSecret(boolean secret) {
        if (!secret) {
            return;
        }
        if (commentWriter instanceof AnonymousWriter) {
            throw new CannotWriteSecretCommentException();
        }
    }

    public void update(
            CommentWriter commentWriter,
            String content,
            boolean secret
    ) {
        validateWriter(commentWriter);
        this.content = content;
        setSecret(secret);
    }

    private void validateWriter(CommentWriter commentWriter) {
        if (!this.commentWriter.equals(commentWriter)) {
            throw new NoAuthorityForCommentException();
        }
    }
}
