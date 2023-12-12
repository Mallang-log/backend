package com.mallang.post.presentation.request;

import com.mallang.post.application.command.UpdateStarGroupHierarchyCommand;
import jakarta.annotation.Nullable;

public record UpdateStarGroupHierarchyRequest(
        @Nullable Long parentGroupId,
        @Nullable Long prevGroupId,
        @Nullable Long nextGroupId
) {
    public UpdateStarGroupHierarchyCommand toCommand(Long groupId, Long memberId) {
        return new UpdateStarGroupHierarchyCommand(
                groupId,
                memberId,
                parentGroupId,
                prevGroupId,
                nextGroupId
        );
    }
}
