package com.mallang.post.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.Category;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.exception.PostLikeCountNegativeException;
import jakarta.annotation.Nullable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends AbstractAggregateRoot<Post> {

    @EmbeddedId
    private PostId postId;

    @MapsId("blogId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blog_id", nullable = false, updatable = false)
    private Blog blog;

    @Embedded
    private PostVisibilityPolicy visibilityPolish;

    @Embedded
    private PostContent content;

    @CreatedDate
    private LocalDateTime createdDate;

    private int likeCount = 0;

    @Builder
    public Post(
            PostId postId,
            Blog blog,
            PostVisibilityPolicy visibilityPolish,
            String title,
            String bodyText,
            String postThumbnailImageName,
            String postIntro,
            @Nullable Category category,
            List<String> tags,
            Member writer
    ) {
        this.postId = postId;
        this.blog = blog;
        this.visibilityPolish = visibilityPolish;
        this.content = new PostContent(title, bodyText, postThumbnailImageName, postIntro, category, tags, writer);
        blog.validateOwner(writer);
    }

    public void update(
            PostVisibilityPolicy visibility,
            String title,
            String bodyText,
            String postThumbnailImageName,
            String intro,
            @Nullable Category category,
            List<String> tags
    ) {
        this.visibilityPolish = visibility;
        this.content.update(title, bodyText, postThumbnailImageName, intro, category, tags);
    }

    public void delete() {
        registerEvent(new PostDeleteEvent(getPostId()));
    }

    public void removeCategory() {
        this.content.removeCategory();
    }

    public void clickLike() {
        this.likeCount++;
    }

    public void cancelLike() {
        if (likeCount == 0) {
            throw new PostLikeCountNegativeException();
        }
        this.likeCount--;
    }

    public boolean isWriter(Member member) {
        return content.isWriter(member);
    }

    public void validateWriter(Member member) {
        if (!isWriter(member)) {
            throw new NoAuthorityPostException();
        }
    }

    public void validateAccess(@Nullable Member member,
                               @Nullable String postPassword) {
        if (isWriter(member)) {
            return;
        }
        if (visibilityPolish.isVisible(postPassword)) {
            return;
        }
        throw new NoAuthorityPostException();
    }

    public String getTitle() {
        return content.getTitle();
    }

    public String getBodyText() {
        return content.getBodyText();
    }

    public String getPostThumbnailImageName() {
        return content.getPostThumbnailImageName();
    }

    public Category getCategory() {
        return content.getCategory();
    }

    public String getPostIntro() {
        return content.getPostIntro();
    }

    public List<String> getTags() {
        return content.getTags();
    }

    public Member getWriter() {
        return content.getWriter();
    }

    public Visibility getVisibility() {
        return visibilityPolish.getVisibility();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post post)) {
            return false;
        }
        return Objects.equals(getPostId(), post.getPostId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPostId());
    }
}
