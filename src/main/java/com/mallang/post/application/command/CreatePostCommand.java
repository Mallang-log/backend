package com.mallang.post.application.command;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostId;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.mallang.post.domain.category.PostCategory;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

@Builder
public record CreatePostCommand(
        Long memberId,
        String blogName,
        String title,
        String intro,
        String bodyText,
        @Nullable String postThumbnailImageName,
        Visibility visibility,
        @Nullable String password,
        @Nullable Long categoryId,
        List<String> tags
) {
    public Post toPost(Member member, PostId postId, Blog blog, @Nullable PostCategory postCategory) {
        return Post.builder()
                .id(postId)
                .blog(blog)
                .visibility(visibility)
                .password(password)
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
