package com.mallang.post.application.command;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.Category;
import com.mallang.post.domain.draft.Draft;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

@Builder
public record CreateDraftCommand(
        Long memberId,
        String blogName,
        String title,
        String bodyText,
        @Nullable String postThumbnailImageName,
        String intro,
        @Nullable Long categoryId,
        List<String> tags
) {
    public Draft toDraft(Member member, Blog blog, @Nullable Category category) {
        return Draft.builder()
                .blog(blog)
                .title(title)
                .bodyText(bodyText)
                .postThumbnailImageName(postThumbnailImageName)
                .postIntro(intro)
                .category(category)
                .tags(tags)
                .writer(member)
                .build();
    }
}
