package com.mallang.post.application.command;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.Category;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostId;
import com.mallang.post.domain.PostIntro;
import com.mallang.post.domain.PostVisibilityPolicy;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

@Builder
public record CreatePostCommand(
        Long memberId,
        String blogName,
        String title,
        String content,
        @Nullable String postThumbnailImageName,
        String intro,
        Visibility visibility,
        @Nullable String password,
        @Nullable Long categoryId,
        List<String> tags
) {
    public Post toPost(Member member, Category category, PostId postId, Blog blog) {
        return Post.builder()
                .postId(postId)
                .blog(blog)
                .title(title)
                .content(content)
                .postThumbnailImageName(postThumbnailImageName)
                .writer(member)
                .visibilityPolish(new PostVisibilityPolicy(visibility, password))
                .category(category)
                .postIntro(new PostIntro(intro))
                .tags(tags)
                .build();
    }
}
