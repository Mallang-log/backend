package com.mallang.post.query.response;

import com.mallang.category.domain.Category;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostContent;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PostManageDetailResponse(
        Long id,
        String title,
        String intro,
        String bodyText,
        @Nullable String postThumbnailImageName,
        Visibility visibility,
        @Nullable String password,
        LocalDateTime createdDate,
        CategoryResponse category,
        TagResponses tags
) {
    public static PostManageDetailResponse from(Post post) {
        return PostManageDetailResponse.builder()
                .id(post.getId().getPostId())
                .title(post.getTitle())
                .intro(post.getPostIntro())
                .bodyText(post.getBodyText())
                .postThumbnailImageName(post.getPostThumbnailImageName())
                .visibility(post.getVisibilityPolish().getVisibility())
                .password(post.getVisibilityPolish().getPassword())
                .createdDate(post.getCreatedDate())
                .category(CategoryResponse.from(post.getContent()))
                .tags(TagResponses.from(post.getContent()))
                .build();
    }

    public record CategoryResponse(
            Long categoryId,
            String categoryName
    ) {
        private static CategoryResponse from(PostContent postContent) {
            Category category = postContent.getCategory();
            if (category == null) {
                return new CategoryResponse(null, null);
            }
            return new CategoryResponse(category.getId(), category.getName());
        }
    }

    public record TagResponses(
            List<String> tagContents
    ) {
        private static TagResponses from(PostContent postContent) {
            return new TagResponses(postContent.getTags());
        }
    }
}
