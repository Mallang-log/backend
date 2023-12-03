package com.mallang.post.domain.draft;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.Category;
import com.mallang.common.domain.CommonRootEntity;
import com.mallang.post.domain.PostContent;
import com.mallang.post.exception.NoAuthorityDraftException;
import jakarta.annotation.Nullable;
import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AssociationOverrides;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Draft extends CommonRootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blog_id", nullable = false, updatable = false)
    private Blog blog;

    @Embedded
    @AssociationOverrides(
            @AssociationOverride(name = "tags", joinTable = @JoinTable(name = "draf_tags"))
    )
    private PostContent content;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Builder
    public Draft(
            Blog blog,
            String title,
            String intro,
            String bodyText,
            @Nullable String postThumbnailImageName,
            @Nullable Category category,
            List<String> tags,
            Member writer
    ) {
        this.blog = blog;
        this.content = new PostContent(title, intro, bodyText, postThumbnailImageName, category, tags, writer);
        blog.validateOwner(writer);
    }

    public void update(
            String title,
            String intro,
            String bodyText,
            @Nullable String postThumbnailImageName,
            @Nullable Category category,
            List<String> tags
    ) {
        this.content.update(title, intro, bodyText, postThumbnailImageName, category, tags);
    }

    public void removeCategory() {
        this.content.removeCategory();
    }

    public void validateWriter(Member member) {
        if (!content.isWriter(member)) {
            throw new NoAuthorityDraftException();
        }
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
