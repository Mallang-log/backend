package com.mallang.post.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.mallang.common.domain.CommonDomainModel;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.NoAuthorityAccessPostException;
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

    public void click(PostLikeValidator postLikeValidator) {
        validatePostVisibility();
        postLikeValidator.validateClickLike(post, member);
        post.clickLike();
    }

    public void cancel() {
        validatePostVisibility();
        post.cancelLike();
    }

    // TODO: 개선
    private void validatePostVisibility() {
        if (post.getVisibilityPolish().getVisibility() == Visibility.PUBLIC) {
            return;
        }
        if (post.getWriter().equals(member)) {
            return;
        }
        throw new NoAuthorityAccessPostException();
    }
}
