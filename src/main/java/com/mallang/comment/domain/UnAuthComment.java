package com.mallang.comment.domain;

import static com.mallang.comment.domain.UnAuthComment.UN_AUTH_COMMENT_TYPE;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.comment.exception.NoAuthorityCommentException;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
@DiscriminatorValue(UN_AUTH_COMMENT_TYPE)
@Entity
public class UnAuthComment extends Comment {

    public static final String UN_AUTH_COMMENT_TYPE = "UnAuthComment";

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Builder
    public UnAuthComment(
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
        post.validateAccess(null, postPassword);
    }

    public void validateUpdate(String password, @Nullable String postPassword) {
        post.validateAccess(null, postPassword);
        validatePassword(password);
    }

    public void validateDelete(
            @Nullable Member member,
            @Nullable String password,
            @Nullable String postPassword
    ) {
        if (post.isWriter(member)) {
            return;
        }
        post.validateAccess(member, postPassword);
        validatePassword(password);
    }

    private void validatePassword(String password) {
        if (!this.password.equals(password)) {
            throw new NoAuthorityCommentException();
        }
    }
}
