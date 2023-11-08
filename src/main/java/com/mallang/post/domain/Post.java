package com.mallang.post.domain;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.Category;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.common.execption.MallangLogException;
import com.mallang.member.domain.Member;
import com.mallang.post.exception.DuplicatedTagsInPostException;
import com.mallang.post.exception.NoAuthorityDeletePostException;
import com.mallang.post.exception.NoAuthorityUpdatePostException;
import com.mallang.post.exception.PostLikeCountNegativeException;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Post extends CommonDomainModel {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    private int likeCount = 0;

    @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "post_id")
    private List<Tag> tags = new ArrayList<>();

    @Builder
    public Post(
            String title,
            String content,
            Member member,
            Blog blog,
            @Nullable Category category,
            List<String> tags
    ) {
        blog.validateOwner(member.getId());
        this.title = title;
        this.content = content;
        this.blog = blog;
        this.member = member;
        setCategory(category);
        setTags(tags);
    }

    private void setCategory(@Nullable Category category) {
        if (category == null) {
            removeCategory();
            return;
        }
        validateOwner(category.getMember().getId(), new NoAuthorityUseCategoryException());
        this.category = category;
    }

    public void removeCategory() {
        this.category = null;
    }

    private void validateOwner(Long memberId, MallangLogException e) {
        if (!member.getId().equals(memberId)) {
            throw e;
        }
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

    public void update(
            Long memberId,
            String title,
            String content,
            @Nullable Category category,
            List<String> tags
    ) {
        validateOwner(memberId, new NoAuthorityUpdatePostException());
        setCategory(category);
        setTags(tags);
        this.title = title;
        this.content = content;
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

    public void delete(Long memberId) {
        validateOwner(memberId, new NoAuthorityDeletePostException());
        registerEvent(new PostDeleteEvent(getId(), getBlog().getId()));
    }
}
