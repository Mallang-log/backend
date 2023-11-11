package com.mallang.post.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.mallang.auth.domain.Member;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PostLike extends CommonDomainModel {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public PostLike(Post post, Member member) {
        this.post = post;
        this.member = member;
    }

    public void click(PostLikeValidator postLikeValidator, @Nullable String postPassword) {
        validateAccessPost(postPassword);
        postLikeValidator.validateClickLike(post, member);
        post.clickLike();
    }

    public void cancel(@Nullable String postPassword) {
        validateAccessPost(postPassword);
        post.cancelLike();
    }

    // TODO 개선 (보호 포스트 중복, 테스트 작성)
    private void validateAccessPost(@Nullable String postPassword) {
        if (post.getVisibilityPolish().getVisibility() == Visibility.PUBLIC) {
            return;
        }
        if (post.getWriter().equals(member)) {
            return;
        }
        if (post.getVisibilityPolish().getVisibility() == Visibility.PROTECTED) {
            if (post.getVisibilityPolish().getPassword().equals(postPassword)) {
                return;
            }
        }
        throw new NoAuthorityAccessPostException();
    }
}
