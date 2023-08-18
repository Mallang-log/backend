package com.mallang.category.query.data;

import com.mallang.category.domain.Category;
import java.util.List;
import lombok.Builder;

@Builder
public record CategoryData(
        Long id,
        String name,
        List<CategoryData> children
) {
    public static CategoryData from(Category category) {
        List<CategoryData> children = category.getChildren().stream()
                .map(CategoryData::from)
                .toList();
        return CategoryData.builder()
                .id(category.getId())
                .name(category.getName())
                .children(children)
                .build();
    }
}
