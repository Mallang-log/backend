package com.mallang.comment.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.exception.NoAuthorityForCommentException;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
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
    }

    // TODO 개선 (보호 포스트 중복, 테스트 작성)
    @Override
    public void write(@Nullable String postPassword) {
        validatePostAccessibility(postPassword);
    }

    public void update(
            Member writer,
            String content,
            boolean secret,
            @Nullable String postPassword
    ) {
        validatePostAccessibility(postPassword);
        validateWriter(writer);
        super.update(content);
        this.secret = secret;
    }

    private void validateWriter(Member writer) {
        if (!writer.equals(getWriter())) {
            throw new NoAuthorityForCommentException();
        }
    }

    public void delete(Member member, CommentDeleteService commentDeleteService, @Nullable String postPassword) {
        validatePostAccessibility(postPassword);
        if (!isPostOwner(member)) {
            validateWriter(member);
        }
        super.delete(commentDeleteService);
    }

    private void validatePostAccessibility(@Nullable String postPassword) {
        PostVisibilityPolicy visibilityPolish = getPost().getVisibilityPolish();
        if (visibilityPolish.getVisibility() == Visibility.PUBLIC) {
            return;
        }
        if (getPost().getWriter().getId().equals(writer.getId())) {
            return;
        }
        if (visibilityPolish.getVisibility() == Visibility.PROTECTED) {
            if (visibilityPolish.getPassword().equals(postPassword)) {
                return;
            }
        }
        throw new NoAuthorityAccessPostException();
    }
}
