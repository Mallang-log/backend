package com.mallang.post.application.command;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.PostCategory;
import com.mallang.post.domain.draft.Draft;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

@Builder
public record CreateDraftCommand(
        Long memberId,
        String blogName,
        String title,
        String intro,
        String bodyText,
        @Nullable String postThumbnailImageName,
        @Nullable Long categoryId,
        List<String> tags
) {
    public Draft toDraft(Member member, Blog blog, @Nullable PostCategory postCategory) {
        return Draft.builder()
                .blog(blog)
                .title(title)
                .intro(intro)
                .bodyText(bodyText)
                .postThumbnailImageName(postThumbnailImageName)
                .category(postCategory)
                .tags(tags)
                .writer(member)
                .build();
    }
}
