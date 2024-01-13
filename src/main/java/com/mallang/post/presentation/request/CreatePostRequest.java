package com.mallang.post.presentation.request;

import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import jakarta.annotation.Nullable;
import java.util.List;

public record CreatePostRequest(
        String blogName,
        String title,
        @Nullable String intro,
        String bodyText,
        @Nullable String postThumbnailImageName,
        Visibility visibility,
        @Nullable String password,
        @Nullable Long categoryId,
        List<String> tags
) {
    public CreatePostCommand toCommand(Long memberId) {
        return CreatePostCommand.builder()
                .memberId(memberId)
                .blogName(blogName)
                .title(title)
                .bodyText(bodyText)
                .postThumbnailImageName(postThumbnailImageName)
                .intro(intro)
                .visibility(visibility)
                .password(password)
                .categoryId(categoryId)
                .tags(tags)
                .build();
    }
}
