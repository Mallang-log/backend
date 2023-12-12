package com.mallang.reference.application.command;

import jakarta.annotation.Nullable;

public record CreateLabelCommand(
        Long memberId,
        String name,
        String colorCode,
        @Nullable Long prevId,
        @Nullable Long nextId
) {
}
