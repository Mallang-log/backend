package com.mallang.post.presentation.request;

import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import jakarta.annotation.Nullable;
import java.util.List;

public record CreatePostRequest(
        Long blogId,
        String title,
        String content,
        @Nullable String postThumbnailImageName,
        String intro,
        Visibility visibility,
        @Nullable String password,
        @Nullable Long categoryId,
        List<String> tags
) {

    public CreatePostCommand toCommand(Long memberId) {
        return CreatePostCommand.builder()
                .memberId(memberId)
                .blogId(blogId)
                .title(title)
                .content(content)
                .postThumbnailImageName(postThumbnailImageName)
                .intro(intro)
                .visibility(visibility)
                .password(password)
                .categoryId(categoryId)
                .tags(tags)
                .build();
    }
}
