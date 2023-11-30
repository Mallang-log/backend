package com.mallang.post.presentation.request;

import com.mallang.post.application.command.UpdateDraftCommand;
import jakarta.annotation.Nullable;
import java.util.List;

public record UpdateDraftRequest(
        String title,
        String bodyText,
        @Nullable String postThumbnailImageName,
        String intro,
        @Nullable Long categoryId,
        List<String> tags
) {

    public UpdateDraftCommand toCommand(Long memberId, Long draftId) {
        return UpdateDraftCommand.builder()
                .memberId(memberId)
                .draftId(draftId)
                .title(title)
                .bodyText(bodyText)
                .postThumbnailImageName(postThumbnailImageName)
                .intro(intro)
                .categoryId(categoryId)
                .tags(tags)
                .build();
    }
}
