package com.mallang.reference.application.command;

import jakarta.annotation.Nullable;

public record UpdateReferenceLinkCommand(
        Long referenceLinkId,
        Long memberId,
        String url,
        String title,
        @Nullable String memo
) {
}
