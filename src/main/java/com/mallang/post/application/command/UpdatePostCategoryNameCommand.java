package com.mallang.post.application.command;

import lombok.Builder;

@Builder
public record UpdatePostCategoryNameCommand(
        Long categoryId,
        Long memberId,
        String name
) {
}
