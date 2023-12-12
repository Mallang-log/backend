package com.mallang.post.presentation.request;

import com.mallang.post.application.command.CreateStarGroupCommand;
import jakarta.annotation.Nullable;

public record CreateStarGroupRequest(
        String name,
        @Nullable Long parentGroupId,
        @Nullable Long prevGroupId,
        @Nullable Long nextGroupId

) {
    public CreateStarGroupCommand toCommand(Long memberId) {
        return new CreateStarGroupCommand(
                memberId,
                name,
                parentGroupId,
                prevGroupId,
                nextGroupId
        );
    }
}
