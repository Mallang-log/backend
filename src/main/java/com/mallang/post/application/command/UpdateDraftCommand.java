package com.mallang.post.application.command;

import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

@Builder
public record UpdateDraftCommand(
        Long memberId,
        Long draftId,
        String title,
        String bodyText,
        @Nullable String postThumbnailImageName,
        String intro,
        @Nullable Long categoryId,
        List<String> tags
) {
}
