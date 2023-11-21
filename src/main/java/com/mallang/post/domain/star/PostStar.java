package com.mallang.post.domain.star;

import static jakarta.persistence.FetchType.LAZY;

import com.mallang.auth.domain.Member;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.post.domain.Post;
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
public class PostStar extends CommonDomainModel {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public PostStar(Post post, Member member) {
        this.post = post;
        this.member = member;
    }

    public void star(PostStarValidator validator, @Nullable String postPassword) {
        post.validatePostAccessibility(member, postPassword);
        validator.validateClickStar(post, member);
    }
}
