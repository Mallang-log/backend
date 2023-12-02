package com.mallang.post.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Embeddable
public class PostId implements Serializable {

    @Column(name = "post_id", nullable = false, updatable = false)
    private Long postId;

    @Column(name = "blog_id", nullable = false, updatable = false)
    private Long blogId;

    public PostId(Long postId, Long blogId) {
        this.postId = postId;
        this.blogId = blogId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostId postId)) {
            return false;
        }
        return Objects.equals(getPostId(), postId.getPostId())
                && Objects.equals(getBlogId(), postId.getBlogId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPostId(), getBlogId());
    }
}
