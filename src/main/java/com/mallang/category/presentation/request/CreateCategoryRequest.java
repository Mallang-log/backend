package com.mallang.category.presentation.request;

import com.mallang.category.application.command.CreateCategoryCommand;

public record CreateCategoryRequest(
        String name,
        Long parentCategoryId
) {
    public CreateCategoryCommand toCommand(Long memberId) {
        return CreateCategoryCommand.builder()
                .name(name)
                .memberId(memberId)
                .parentCategoryId(parentCategoryId)
                .build();
    }
}
