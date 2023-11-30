package com.mallang.post.application.command;

public record DeleteDraftCommand(
        Long memberId,
        Long draftId
) {
}
