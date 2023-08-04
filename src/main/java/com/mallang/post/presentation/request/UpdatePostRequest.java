package com.mallang.post.presentation.request;

import com.mallang.post.application.command.UpdatePostCommand;

public record UpdatePostRequest(
        String title,
        String content
) {
    public UpdatePostCommand toCommand(Long memberId, Long postId) {
        return UpdatePostCommand.builder()
                .memberId(memberId)
                .postId(postId)
                .title(title)
                .content(content)
                .build();
    }
}
