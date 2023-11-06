package com.mallang.post.query.data;

import com.mallang.category.domain.Category;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PostDetailData(
        Long id,
        String title,
        String content,
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
            Member member = post.getMember();
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
            return new TagDetailInfos(post.getTags().stream()
                    .map(Tag::getContent)
                    .toList());
        }
    }
}
