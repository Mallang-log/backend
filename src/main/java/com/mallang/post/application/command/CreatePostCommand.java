package com.mallang.post.application.command;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.Category;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

@Builder
public record CreatePostCommand(
        Long memberId,
        Long blogId,
        String title,
        String content,
        Visibility visibility,
        @Nullable String password,
        @Nullable Long categoryId,
        List<String> tags
) {
    public Post toPost(Member member, Blog blog, Category category, Long postIdInBlog) {
        return Post.builder()
                .title(title)
                .content(content)
                .order(postIdInBlog)
                .writer(member)
                .visibilityPolish(new PostVisibilityPolicy(visibility, password))
                .blog(blog)
                .category(category)
                .tags(tags)
                .build();
    }
}
