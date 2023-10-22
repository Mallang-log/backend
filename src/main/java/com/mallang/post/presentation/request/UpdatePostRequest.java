package com.mallang.post.presentation.request;

import com.mallang.post.application.command.UpdatePostCommand;
import jakarta.annotation.Nullable;
import java.util.List;

public record UpdatePostRequest(
        String title,
        String content,
        @Nullable Long categoryId,
        List<String> tags
) {
    public UpdatePostCommand toCommand(Long memberId, Long postId) {
        return UpdatePostCommand.builder()
                .memberId(memberId)
                .postId(postId)
                .title(title)
                .content(content)
                .categoryId(categoryId)
                .tags(tags)
                .build();
    }
}
