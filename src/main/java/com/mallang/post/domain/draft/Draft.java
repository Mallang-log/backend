package com.mallang.post.domain.draft;

import static jakarta.persistence.FetchType.LAZY;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.Category;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.post.domain.PostContent;
import com.mallang.post.domain.PostIntro;
import jakarta.annotation.Nullable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Draft extends CommonDomainModel {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blog_id", nullable = false, updatable = false)
    private Blog blog;

    @Embedded
    private PostContent content;

    @Builder
    public Draft(
            Blog blog,
            String title,
            String bodyText,
            String postThumbnailImageName,
            PostIntro postIntro,
            @Nullable Category category,
            List<String> tags,
            Member writer
    ) {
        this.blog = blog;
        this.content = new PostContent(title, bodyText, postThumbnailImageName, postIntro, category, tags, writer);
        blog.validateOwner(writer);
    }

    public void update(
            String title,
            String bodyText,
            String postThumbnailImageName,
            PostIntro intro,
            @Nullable Category category,
            List<String> tags
    ) {
        this.content.update(title, bodyText, postThumbnailImageName, intro, category, tags);
    }

    public void removeCategory() {
        this.content.removeCategory();
    }

    public void validateWriter(Member member) {
        content.validateWriter(member);
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
}
