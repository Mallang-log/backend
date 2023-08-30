package com.mallang.comment.domain;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.comment.domain.writer.AuthenticatedWriterCredential;
import com.mallang.comment.domain.writer.CommentWriter;
import com.mallang.comment.domain.writer.WriterCredential;
import com.mallang.comment.exception.CannotWriteSecretCommentException;
import com.mallang.comment.exception.CommentDepthConstraintViolationException;
import com.mallang.comment.exception.DifferentPostFromParentCommentException;
import com.mallang.comment.exception.NoAuthorityForCommentException;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.post.domain.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private boolean deleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parant_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    @Builder
    private Comment(
            String content,
            CommentWriter commentWriter,
            Post post,
            boolean secret,
            Comment parent
    ) {
        this.content = content;
        this.commentWriter = commentWriter;
        this.post = post;
        setSecret(secret);
        setParent(parent);
    }

    public void setSecret(boolean secret) {
        validateSecret(secret);
        this.secret = secret;
    }

    private void validateSecret(boolean secret) {
        if (!secret) {
            return;
        }
        if (!commentWriter.canWriteSecret()) {
            throw new CannotWriteSecretCommentException();
        }
    }

    private void setParent(Comment parent) {
        if (willBeParent(parent)) {
            unlinkFromParent();
            return;
        }
        beChild(parent);
    }

    private boolean willBeParent(Comment parent) {
        return parent == null;
    }

    private void unlinkFromParent() {
        if (this.parent != null) {
            this.parent.removeChild(this);
            this.parent = null;
        }
    }

    private void removeChild(Comment child) {
        this.children.remove(child);
    }

    private void beChild(Comment parent) {
        validateSamePost(parent);
        validateCommentDepthConstraint(parent);
        this.parent = parent;
        parent.addChild(this);
    }

    private void validateSamePost(Comment parent) {
        if (!Objects.equals(post, parent.post)) {
            throw new DifferentPostFromParentCommentException();
        }
    }

    private void validateCommentDepthConstraint(Comment parent) {
        if (parent.parent != null) {
            throw new CommentDepthConstraintViolationException();
        }
    }

    private void addChild(Comment child) {
        this.children.add(child);
    }

    public void update(
            WriterCredential writerCredential,
            String content,
            boolean secret
    ) {
        validateWriter(writerCredential);
        this.content = content;
        setSecret(secret);
    }

    private void validateWriter(WriterCredential writerCredential) {
        if (!commentWriter.hasAuthority(writerCredential)) {
            throw new NoAuthorityForCommentException();
        }
    }

    public void delete(WriterCredential writerCredential) {
        if (!isPostOwner(writerCredential)) {
            validateWriter(writerCredential);
        }
        if (isChild()) {
            unlinkFromParent();
        }
        this.deleted = true;
    }

    private boolean isPostOwner(WriterCredential writerCredential) {
        if (writerCredential instanceof AuthenticatedWriterCredential authenticatedWriterCredential) {
            return authenticatedWriterCredential.memberId().equals(post.getMember().getId());
        }
        return false;
    }

    private boolean isChild() {
        return parent != null;
    }
}
