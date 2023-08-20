package com.mallang.post.query.data;

public record PostSearchCond(
        Long categoryId,
        String tag,
        Long writerId
) {
}
