package com.mallang.reference.application.command;

import jakarta.annotation.Nullable;

public record UpdateLabelHierarchyCommand(
        Long memberId,
        Long labelId,
        @Nullable Long prevId,
        @Nullable Long nextId
) {
}
