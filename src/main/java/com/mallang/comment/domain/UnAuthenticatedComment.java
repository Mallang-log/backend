package com.mallang.comment.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.exception.NoAuthorityForCommentException;
import com.mallang.post.domain.Post;
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

    @Override
    public void write(@Nullable String postPassword) {
        post.validatePostAccessibility(null, postPassword);
    }

    public void update(
            String password,
            String content,
            @Nullable String postPassword
    ) {
        post.validatePostAccessibility(null, postPassword);
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
        post.validatePostAccessibility(null, postPassword);
        if (!isPostOwner(member)) {
            validatePassword(password);
        }
        super.delete(commentDeleteService);
    }
}
