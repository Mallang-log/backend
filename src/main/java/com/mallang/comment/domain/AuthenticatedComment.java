package com.mallang.comment.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.exception.NoAuthorityForCommentException;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.NoAuthorityAccessPostException;
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
        validatePostVisibility();
    }

    // TODO: 개선
    private void validatePostVisibility() {
        if (getPost().getVisibilityPolish().getVisibility() == Visibility.PUBLIC) {
            return;
        }
        if (getPost().getWriter().equals(writer)) {
            return;
        }
        throw new NoAuthorityAccessPostException();
    }

    public void update(
            Member writer,
            String content,
            boolean secret
    ) {
        validatePostVisibility();
        validateWriter(writer);
        super.update(content);
        this.secret = secret;
    }

    private void validateWriter(Member writer) {
        if (!writer.equals(getWriter())) {
            throw new NoAuthorityForCommentException();
        }
    }

    public void delete(Member member, CommentDeleteService commentDeleteService) {
        validatePostVisibility();
        if (!isPostOwner(member)) {
            validateWriter(member);
        }
        super.delete(commentDeleteService);
    }
}
