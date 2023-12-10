package com.mallang.post.application.command;

import lombok.Builder;

@Builder
public record DeletePostCategoryCommand(
        Long memberId,
        Long categoryId
) {
}
