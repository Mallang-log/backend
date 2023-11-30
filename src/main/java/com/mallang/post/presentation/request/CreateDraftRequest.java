package com.mallang.post.presentation.request;

import com.mallang.post.application.command.CreateDraftCommand;
import jakarta.annotation.Nullable;
import java.util.List;

public record CreateDraftRequest(
        String blogName,
        String title,
        String bodyText,
        @Nullable String postThumbnailImageName,
        String intro,
        @Nullable Long categoryId,
        List<String> tags
) {
    public CreateDraftCommand toCommand(Long memberId) {
        return CreateDraftCommand.builder()
                .memberId(memberId)
                .blogName(blogName)
                .title(title)
                .bodyText(bodyText)
                .postThumbnailImageName(postThumbnailImageName)
                .intro(intro)
                .categoryId(categoryId)
                .tags(tags)
                .build();
    }
}
