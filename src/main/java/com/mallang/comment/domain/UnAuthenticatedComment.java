package com.mallang.comment.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.exception.NoAuthorityForCommentException;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class UnAuthenticatedComment extends Comment {

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Builder
    public UnAuthenticatedComment(
            String content,
            Post post,
            @Nullable Comment parent,
            String nickname,
            String password
    ) {
        super(content, post, parent);
        this.nickname = nickname;
        this.password = password;
    }

    // TODO 개선 (보호 포스트 중복, 테스트 작성)
    @Override
    public void write(@Nullable String postPassword) {
        validatePostAccessibility(postPassword);
    }

    public void update(
            String password,
            String content,
            @Nullable String postPassword
    ) {
        validatePostAccessibility(postPassword);
        validatePassword(password);
        super.update(content);
    }

    private void validatePassword(String password) {
        if (!this.password.equals(password)) {
            throw new NoAuthorityForCommentException();
        }
    }

    public void delete(
            @Nullable Member member,
            @Nullable String password,
            CommentDeleteService commentDeleteService,
            @Nullable String postPassword
    ) {
        validatePostAccessibility(postPassword);
        if (!isPostOwner(member)) {
            validatePassword(password);
        }
        super.delete(commentDeleteService);
    }

    private void validatePostAccessibility(@Nullable String postPassword) {
        PostVisibilityPolicy visibilityPolish = getPost().getVisibilityPolish();
        if (visibilityPolish.getVisibility() == Visibility.PUBLIC) {
            return;
        }
        if (visibilityPolish.getVisibility() == Visibility.PRIVATE) {
            throw new NoAuthorityAccessPostException();
        }
        if (visibilityPolish.getVisibility() == Visibility.PROTECTED) {
            if (!visibilityPolish.getPassword().equals(postPassword)) {
                throw new NoAuthorityAccessPostException();
            }
        }
    }
}
