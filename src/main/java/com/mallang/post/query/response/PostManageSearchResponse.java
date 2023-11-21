package com.mallang.post.query.response;

import com.mallang.category.domain.Category;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PostManageSearchResponse(
        Long id,
        String title,
        Visibility visibility,
        String password,
        LocalDateTime createdDate,
        CategoryResponse category
) {
    public static PostManageSearchResponse from(Post post) {
        return PostManageSearchResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .visibility(post.getVisibilityPolish().getVisibility())
                .password(post.getVisibilityPolish().getPassword())
                .createdDate(post.getCreatedDate())
                .category(CategoryResponse.from(post))
                .build();
    }

    public record CategoryResponse(
            Long categoryId,
            String categoryName
    ) {
        private static CategoryResponse from(Post post) {
            Category category = post.getCategory();
            if (category == null) {
                return new CategoryResponse(null, null);
            }
            return new CategoryResponse(category.getId(), category.getName());
        }
    }
}
