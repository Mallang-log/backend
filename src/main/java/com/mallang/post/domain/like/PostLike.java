package com.mallang.post.domain.like;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.mallang.auth.domain.Member;
import com.mallang.common.domain.CommonRootEntity;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PostLike extends CommonRootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id", nullable = false, updatable = false)
    @JoinColumn(name = "blog_id", referencedColumnName = "blog_id", nullable = false, updatable = false)
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public PostLike(Post post, Member member) {
        this.post = post;
        this.member = member;
    }

    public void like(PostLikeValidator postLikeValidator, @Nullable String postPassword) {
        post.validateAccess(member, postPassword);
        postLikeValidator.validateClickLike(post, member);
        post.clickLike();
    }

    public void cancel(@Nullable String postPassword) {
        post.validateAccess(member, postPassword);
        post.cancelLike();
    }
}
