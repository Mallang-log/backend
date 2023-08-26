package com.mallang.post.query.data;

import lombok.Builder;

@Builder
public record PostSearchCond(
        Long categoryId,
        String tag,
        Long writerId,
        String title,
        String content,
        String titleOrContent
) {
}
