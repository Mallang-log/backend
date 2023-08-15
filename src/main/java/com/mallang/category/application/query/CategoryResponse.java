package com.mallang.category.application.query;

import com.mallang.category.domain.Category;
import java.util.List;
import lombok.Builder;

@Builder
public record CategoryResponse(
        Long id,
        String name,
        List<CategoryResponse> children
) {
    public static CategoryResponse from(Category category) {
        List<CategoryResponse> children = category.getChildren().stream()
                .map(CategoryResponse::from)
                .toList();
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .children(children)
                .build();
    }
}
