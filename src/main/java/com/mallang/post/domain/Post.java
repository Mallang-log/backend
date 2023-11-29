package com.mallang.post.domain;

import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.Category;
import com.mallang.post.exception.DuplicatedTagsInPostException;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.exception.PostLikeCountNegativeException;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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

    @MapsId(value = "blog_id")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blog_id", nullable = false, updatable = false)
    private Blog blog;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bodyText;

    @Column(nullable = true)
    private String postThumbnailImageName;

    @Embedded
    private PostIntro postIntro;

    @Embedded
    private PostVisibilityPolicy visibilityPolish;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    private int likeCount = 0;

    @OneToMany(cascade = {PERSIST, MERGE, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id", updatable = false, nullable = false)
    @JoinColumn(name = "blog_id", referencedColumnName = "blog_id", updatable = false, nullable = false)
    private List<Tag> tags = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdDate;

    @Builder
    public Post(
            PostId postId,
            Blog blog,
            String title,
            String bodyText,
            String postThumbnailImageName,
            PostIntro postIntro,
            PostVisibilityPolicy visibilityPolish,
            Member writer,
            @Nullable Category category,
            List<String> tags
    ) {
        this.postId = postId;
        this.blog = blog;
        this.title = title;
        this.bodyText = bodyText;
        this.postThumbnailImageName = postThumbnailImageName;
        this.postIntro = postIntro;
        this.visibilityPolish = visibilityPolish;
        this.writer = writer;
        blog.validateOwner(writer);
        setCategory(category);
        setTags(tags);
    }

    public void update(
            String title,
            String content,
            String postThumbnailImageName,
            PostIntro intro,
            PostVisibilityPolicy visibility,
            @Nullable Category category,
            List<String> tags
    ) {
        this.title = title;
        this.bodyText = content;
        this.postThumbnailImageName = postThumbnailImageName;
        this.postIntro = intro;
        this.visibilityPolish = visibility;
        setCategory(category);
        setTags(tags);
    }

    public void delete() {
        registerEvent(new PostDeleteEvent(getPostId()));
    }

    private void setTags(List<String> tags) {
        this.tags.clear();
        if (tags == null) {
            return;
        }
        validateDuplicateTags(tags);
        tags.stream()
                .map(Tag::new)
                .forEach(it -> this.tags.add(it));
    }

    private void setCategory(@Nullable Category category) {
        if (category == null) {
            this.category = null;
            return;
        }
        category.validateOwner(writer);
        this.category = category;
    }

    private void validateDuplicateTags(List<String> list) {
        HashSet<String> distinct = new HashSet<>(list);
        if (distinct.size() != list.size()) {
            throw new DuplicatedTagsInPostException();
        }
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

    public void validateWriter(Member member) {
        if (!writer.equals(member)) {
            throw new NoAuthorityPostException();
        }
    }

    public void validatePostAccessibility(@Nullable Member member,
                                          @Nullable String postPassword) {
        if (visibilityPolish.getVisibility() == PUBLIC) {
            return;
        }
        if (getWriter().equals(member)) {
            return;
        }
        if (visibilityPolish.getVisibility() == PROTECTED
                && visibilityPolish.getPassword().equals(postPassword)) {
            return;
        }
        throw new NoAuthorityAccessPostException();
    }

    public void removeCategory() {
        this.category = null;
    }

    public String getPostIntro() {
        return postIntro.getIntro();
    }

    public List<String> getTags() {
        return tags.stream()
                .map(Tag::getContent)
                .toList();
    }

    @Override
    public List<Object> domainEvents() {
        return new ArrayList<>(super.domainEvents());
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
