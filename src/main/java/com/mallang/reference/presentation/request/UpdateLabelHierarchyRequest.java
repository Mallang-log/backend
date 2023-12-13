package com.mallang.reference.presentation.request;

import com.mallang.reference.application.command.UpdateLabelHierarchyCommand;
import jakarta.annotation.Nullable;

public record UpdateLabelHierarchyRequest(
        @Nullable Long prevSiblingId,
        @Nullable Long nextSiblingId
) {
    public UpdateLabelHierarchyCommand toCommand(Long labelId, Long memberId) {
        return new UpdateLabelHierarchyCommand(
                memberId,
                labelId,
                prevSiblingId,
                nextSiblingId
        );
    }
}
