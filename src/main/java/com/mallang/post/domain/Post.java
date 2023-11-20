package com.mallang.post.domain;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.Category;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.DuplicatedTagsInPostException;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import com.mallang.post.exception.PostLikeCountNegativeException;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
        name = "post",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"orders", "blog_id"}
                )
        }
)
@Entity
public class Post extends CommonDomainModel {

    // TODO https://github.com/jakartaee/persistence/issues/113 해당 이슈 해결되면, 해당 방법 사용해서 자동 생성되도록 수정하기
    @Column(name = "orders", nullable = false, updatable = false)
    private Long order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

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

    @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "post_id", updatable = false, nullable = false)
    private List<Tag> tags = new ArrayList<>();

    @Builder
    public Post(
            Long order,
            Blog blog,
            String title,
            String content,
            String postThumbnailImageName,
            PostIntro postIntro,
            PostVisibilityPolicy visibilityPolish,
            Member writer,
            @Nullable Category category,
            List<String> tags
    ) {
        this.order = order;
        this.blog = blog;
        this.title = title;
        this.content = content;
        this.postThumbnailImageName = postThumbnailImageName;
        this.postIntro = postIntro;
        this.visibilityPolish = visibilityPolish;
        this.writer = writer;
        this.category = category;
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
        setTags(tags);
        this.title = title;
        this.content = content;
        this.postThumbnailImageName = postThumbnailImageName;
        this.postIntro = intro;
        this.visibilityPolish = visibility;
        this.category = category;
    }

    public void delete() {
        registerEvent(new PostDeleteEvent(getId()));
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

    public void validatePostAccessibility(@Nullable Member member,
                                          @Nullable String postPassword) {
        if (visibilityPolish.getVisibility() == Visibility.PUBLIC) {
            return;
        }
        if (getWriter().equals(member)) {
            return;
        }
        if (visibilityPolish.getVisibility() == Visibility.PROTECTED) {
            if (visibilityPolish.getPassword().equals(postPassword)) {
                return;
            }
        }
        throw new NoAuthorityAccessPostException();
    }

    public void removeCategory() {
        this.category = null;
    }

    public String getPostIntro() {
        return postIntro.getIntro();
    }
}
