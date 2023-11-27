package com.mallang.post.presentation.request;

import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import jakarta.annotation.Nullable;
import java.util.List;

public record UpdatePostRequest(
        String blogName,
        String title,
        String content,
        @Nullable String postThumbnailImageName,
        String intro,
        Visibility visibility,
        String password,
        @Nullable Long categoryId,
        List<String> tags
) {

    public UpdatePostCommand toCommand(Long memberId, Long postId) {
        return UpdatePostCommand.builder()
                .memberId(memberId)
                .postId(postId)
                .blogName(blogName)
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
