package com.mallang.category.presentation.request;

import com.mallang.category.application.command.UpdatePostCategoryNameCommand;

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
