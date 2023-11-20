package com.mallang.post.query.data;

import com.mallang.category.domain.Category;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PostManageSimpleData(
        Long id,
        String title,
        Visibility visibility,
        String password,
        LocalDateTime createdDate,
        CategoryManageSimpleInfo categoryInfo
) {

    public record CategoryManageSimpleInfo(
            Long categoryId,
            String categoryName
    ) {
        private static CategoryManageSimpleInfo from(Post post) {
            Category category = post.getCategory();
            if (category == null) {
                return new CategoryManageSimpleInfo(null, null);
            }
            return new CategoryManageSimpleInfo(category.getId(), category.getName());
        }
    }

    public static PostManageSimpleData from(Post post) {
        return PostManageSimpleData.builder()
                .id(post.getId())
                .title(post.getTitle())
                .visibility(post.getVisibilityPolish().getVisibility())
                .password(post.getVisibilityPolish().getPassword())
                .createdDate(post.getCreatedDate())
                .categoryInfo(CategoryManageSimpleInfo.from(post))
                .build();
    }
}
