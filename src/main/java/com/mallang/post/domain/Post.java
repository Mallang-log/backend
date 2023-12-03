package com.mallang.post.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.Category;
import com.mallang.common.domain.CommonRootEntity;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.exception.PostLikeCountNegativeException;
import jakarta.annotation.Nullable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Post extends CommonRootEntity<PostId> {

    @EmbeddedId
    private PostId id;

    @MapsId("blogId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blog_id", nullable = false, updatable = false)
    private Blog blog;

    @Embedded
    private PostVisibilityPolicy visibilityPolish;

    @Embedded
    private PostContent content;

    private int likeCount = 0;

    @Builder
    public Post(
            PostId id,
            Blog blog,
            PostVisibilityPolicy visibilityPolish,
            String title,
            String intro,
            String bodyText,
            @Nullable String postThumbnailImageName,
            @Nullable Category category,
            List<String> tags,
            Member writer
    ) {
        this.id = id;
        this.blog = blog;
        this.visibilityPolish = visibilityPolish;
        this.content = new PostContent(title, intro, bodyText, postThumbnailImageName, category, tags, writer);
        blog.validateOwner(writer);
    }

    public void update(
            PostVisibilityPolicy visibility,
            String title,
            String intro,
            String bodyText,
            @Nullable String postThumbnailImageName,
            @Nullable Category category,
            List<String> tags
    ) {
        this.visibilityPolish = visibility;
        this.content.update(title, intro, bodyText, postThumbnailImageName, category, tags);
    }

    public void delete() {
        registerEvent(new PostDeleteEvent(getId()));
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
        return Objects.equals(getId(), post.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
