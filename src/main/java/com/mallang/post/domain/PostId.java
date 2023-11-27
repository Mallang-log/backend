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
    private Long id;

    @Column(name = "blog_id", nullable = false, updatable = false)
    private Long blogId;

    public PostId(Long id, Long blogId) {
        this.id = id;
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
        return Objects.equals(getId(), postId.getId())
                && Objects.equals(getBlogId(), postId.getBlogId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getBlogId());
    }
}
