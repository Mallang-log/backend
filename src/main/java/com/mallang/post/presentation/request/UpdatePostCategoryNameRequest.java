package com.mallang.post.presentation.request;

import com.mallang.post.application.command.UpdatePostCategoryNameCommand;

public record UpdatePostCategoryNameRequest(
        String name
) {
    public UpdatePostCategoryNameCommand toCommand(Long categoryId, Long memberId) {
        return new UpdatePostCategoryNameCommand(
                categoryId,
                memberId,
                name
        );
    }
}
