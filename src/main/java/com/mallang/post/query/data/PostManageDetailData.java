package com.mallang.post.query.data;

import com.mallang.category.domain.Category;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PostManageDetailData(
        Long id,
        String title,
        String intro,
        String content,
        @Nullable String postThumbnailImageName,
        Visibility visibility,
        @Nullable String password,
        LocalDateTime createdDate,
        CategoryDetailInfo categoryInfo,
        TagDetailInfos tagDetailInfos
) {

    public static PostManageDetailData from(Post post) {
        return PostManageDetailData.builder()
                .id(post.getId())
                .title(post.getTitle())
                .intro(post.getPostIntro())
                .content(post.getContent())
                .postThumbnailImageName(post.getPostThumbnailImageName())
                .visibility(post.getVisibilityPolish().getVisibility())
                .password(post.getVisibilityPolish().getPassword())
                .createdDate(post.getCreatedDate())
                .categoryInfo(CategoryDetailInfo.from(post))
                .tagDetailInfos(TagDetailInfos.from(post))
                .build();
    }

    public record CategoryDetailInfo(
            Long categoryId,
            String categoryName
    ) {
        private static CategoryDetailInfo from(Post post) {
            Category category = post.getCategory();
            if (category == null) {
                return new CategoryDetailInfo(null, null);
            }
            return new CategoryDetailInfo(category.getId(), category.getName());
        }
    }

    public record TagDetailInfos(
            List<String> tagContents
    ) {
        private static TagDetailInfos from(Post post) {
            return new TagDetailInfos(post.getTags());
        }
    }
}
