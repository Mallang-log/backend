package com.mallang.category.presentation.request;

import com.mallang.category.application.command.UpdateCategoryNameCommand;

public record UpdateCategoryNameRequest(
        String name
) {
    public UpdateCategoryNameCommand toCommand(Long categoryId, Long memberId) {
        return new UpdateCategoryNameCommand(
                categoryId,
                memberId,
                name
        );
    }
}
