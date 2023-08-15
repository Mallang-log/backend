package com.mallang.post.application.query;

import com.mallang.category.domain.Category;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PostDetailResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdDate,
        WriterDetailInfo writerInfo,
        CategoryDetailInfo categoryInfo
) {

    public static PostDetailResponse from(Post post) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdDate(post.getCreatedDate())
                .writerInfo(WriterDetailInfo.from(post))
                .categoryInfo(CategoryDetailInfo.from(post))
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
}
