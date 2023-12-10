package com.mallang.post.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.post.domain.category.PostCategory;
import com.mallang.post.exception.DuplicatedTagsInPostException;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class PostContent {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bodyText;

    @Column(nullable = true)
    private String postThumbnailImageName;

    @Embedded
    private PostIntro postIntro;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private PostCategory category;

    @ElementCollection
    private List<Tag> tags = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writer;

    @Builder
    public PostContent(
            String title,
            String postIntro,
            String bodyText,
            @Nullable String postThumbnailImageName,
            @Nullable PostCategory category,
            List<String> tags,
            Member writer
    ) {
        this.title = title;
        this.postIntro = new PostIntro(postIntro);
        this.bodyText = bodyText;
        this.postThumbnailImageName = postThumbnailImageName;
        this.writer = writer;
        setPostCategory(category);
        setTags(tags);
    }

    private void setPostCategory(@Nullable PostCategory category) {
        if (category == null) {
            this.category = null;
            return;
        }
        category.validateOwner(writer);
        this.category = category;
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

    public boolean isWriter(Member member) {
        return writer.equals(member);
    }

    public PostContent removeCategory() {
        return new PostContent(
                getTitle(),
                getPostIntro(),
                getBodyText(),
                getPostThumbnailImageName(),
                null,
                getTags(),
                getWriter()
        );
    }

    public String getPostIntro() {
        return postIntro.getIntro();
    }

    public List<String> getTags() {
        return tags.stream()
                .map(Tag::getContent)
                .toList();
    }
}
