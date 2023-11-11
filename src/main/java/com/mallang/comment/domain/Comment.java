package com.mallang.comment.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.exception.CommentDepthConstraintViolationException;
import com.mallang.comment.exception.DifferentPostFromParentCommentException;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class Comment extends CommonDomainModel {

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private boolean deleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parant_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    protected Comment(
            String content,
            Post post,
            @Nullable Comment parent
    ) {
        this.content = content;
        this.post = post;
        if (parent != null) {
            beChild(parent);
        }
    }

    private void beChild(Comment parent) {
        validateSamePost(parent);
        validateCommentDepthConstraint(parent);
        this.parent = parent;
        parent.getChildren().add(this);
    }

    private void validateSamePost(Comment parent) {
        if (!Objects.equals(post, parent.post)) {
            throw new DifferentPostFromParentCommentException();
        }
    }

    private void validateCommentDepthConstraint(Comment parent) {
        if (parent.getParent() != null) {
            throw new CommentDepthConstraintViolationException();
        }
    }

    public abstract void write(@Nullable String postPassword);

    protected void update(String content) {
        this.content = content;
    }

    public void delete(CommentDeleteService commentDeleteService) {
        this.deleted = true;
        commentDeleteService.delete(this);
    }

    public boolean isChild() {
        return parent != null;
    }

    public void unlinkFromParent() {
        this.parent.getChildren().remove(this);
        this.parent = null;
    }

    protected boolean isPostOwner(Member member) {
        return getPost().getWriter().equals(member);
    }
}
