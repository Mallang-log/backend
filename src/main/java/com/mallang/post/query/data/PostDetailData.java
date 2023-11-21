package com.mallang.post.query.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mallang.auth.domain.Member;
import com.mallang.category.domain.Category;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PostDetailData(
        Long id,
        String title,
        String content,
        @Nullable String postThumbnailImageName,
        Visibility visibility,
        boolean isProtected,
        @JsonIgnore String password,
        int likeCount,
        boolean isLiked,
        LocalDateTime createdDate,
        WriterDetailInfo writerInfo,
        CategoryDetailInfo categoryInfo,
        TagDetailInfos tagDetailInfos
) {

    public static PostDetailData from(Post post) {
        return withLiked(post, false);
    }

    public static PostDetailData withLiked(Post post, boolean isLiked) {
        return PostDetailData.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postThumbnailImageName(post.getPostThumbnailImageName())
                .visibility(post.getVisibilityPolish().getVisibility())
                .password(post.getVisibilityPolish().getPassword())
                .likeCount(post.getLikeCount())
                .isLiked(isLiked)
                .createdDate(post.getCreatedDate())
                .writerInfo(WriterDetailInfo.from(post))
                .categoryInfo(CategoryDetailInfo.from(post))
                .tagDetailInfos(TagDetailInfos.from(post))
                .build();
    }

    public record WriterDetailInfo(
            Long writerId,
            String writerNickname,
            String writerProfileImageUrl
    ) {
        private static WriterDetailInfo from(Post post) {
            Member member = post.getWriter();
            return new WriterDetailInfo(
                    member.getId(),
                    member.getNickname(),
                    member.getProfileImageUrl()
            );
        }
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
