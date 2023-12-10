package com.mallang.post.query.response;

import com.mallang.post.domain.category.PostCategory;
import java.util.List;
import lombok.Builder;

@Builder
public record PostCategoryResponse(
        Long id,
        String name,
        List<PostCategoryResponse> children
) {
    public static PostCategoryResponse from(PostCategory postCategory) {
        List<PostCategoryResponse> children = postCategory.getSortedChildren()
                .stream()
                .map(PostCategoryResponse::from)
                .toList();
        return PostCategoryResponse.builder()
                .id(postCategory.getId())
                .name(postCategory.getName())
                .children(children)
                .build();
    }
}
