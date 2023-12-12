package com.mallang.post.presentation.request;

import com.mallang.post.application.command.UpdateStarGroupNameCommand;

public record UpdateStarGroupNameRequest(
        String name
) {
    public UpdateStarGroupNameCommand toCommand(Long groupId, Long memberId) {
        return new UpdateStarGroupNameCommand(
                groupId,
                memberId,
                name
        );
    }
}
