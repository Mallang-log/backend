package com.mallang.post.application.command;

import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogName;
import com.mallang.category.domain.Category;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

@Builder
public record CreatePostCommand(
        Long memberId,
        BlogName blogName,
        String title,
        String content,
        @Nullable Long categoryId,
        List<String> tags
) {
    public Post toPost(Member member, Blog blog, Category category) {
        return Post.builder()
                .title(title)
                .content(content)
                .member(member)
                .blog(blog)
                .category(category)
                .tags(tags)
                .build();
    }
}
