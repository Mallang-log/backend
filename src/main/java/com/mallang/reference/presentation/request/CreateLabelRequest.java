package com.mallang.reference.presentation.request;

import com.mallang.reference.application.command.CreateLabelCommand;
import jakarta.annotation.Nullable;

public record CreateLabelRequest(
        String name,
        String colorCode,
        @Nullable Long prevSiblingId,
        @Nullable Long nextSiblingId
) {
    public CreateLabelCommand toCommand(Long memberId) {
        return new CreateLabelCommand(
                memberId,
                name,
                colorCode,
                prevSiblingId,
                nextSiblingId
        );
    }
}
