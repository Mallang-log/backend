package com.mallang.comment.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.exception.CommentDepthConstraintViolationException;
import com.mallang.common.domain.CommonRootEntity;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class Comment extends CommonRootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    protected Long id;

    @Column(nullable = false)
    protected String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id", nullable = false, updatable = false)
    @JoinColumn(name = "blog_id", referencedColumnName = "blog_id", nullable = false, updatable = false)
    protected Post post;

    protected boolean deleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parant_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    protected Comment parent;

    @OneToMany(mappedBy = "parent")
    protected List<Comment> children = new ArrayList<>();

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
        validateCommentDepthConstraint(parent);
        this.parent = parent;
        parent.getChildren().add(this);
    }

    private void validateCommentDepthConstraint(Comment parent) {
        if (parent.getParent() != null) {
            throw new CommentDepthConstraintViolationException();
        }
    }

    public abstract void write(@Nullable String postPassword);

    public void update(String content) {
        this.content = content;
    }

    public void delete(CommentDeleteService commentDeleteService) {
        this.deleted = true;
        this.content = "삭제된 댓글입니다.";
        commentDeleteService.delete(this);
    }

    public boolean isChild() {
        return parent != null;
    }

    public void unlinkFromParent() {
        this.parent.getChildren().remove(this);
        this.parent = null;
    }
}
