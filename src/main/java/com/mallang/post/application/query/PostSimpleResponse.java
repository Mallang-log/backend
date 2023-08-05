package com.mallang.post.application.query;

import com.mallang.category.domain.Category;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PostSimpleResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdDate,
        WriterSimpleInfo writerInfo,
        CategorySimpleInfo categoryInfo
) {

    public static PostSimpleResponse from(Post post) {
        return PostSimpleResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdDate(post.getCreatedDate())
                .writerInfo(WriterSimpleInfo.from(post))
                .categoryInfo(CategorySimpleInfo.from(post))
                .build();
    }

    public record WriterSimpleInfo(
            Long writerId,
            String writerNickname,
            String writerProfileImageUrl
    ) {
        private static WriterSimpleInfo from(Post post) {
            Member member = post.getMember();
            return new WriterSimpleInfo(
                    member.getId(),
                    member.getNickname(),
                    member.getProfileImageUrl()
            );
        }
    }

    public record CategorySimpleInfo(
            Long categoryId,
            String categoryName
    ) {
        private static CategorySimpleInfo from(Post post) {
            Category category = post.getCategory();
            if (category == null) {
                return new CategorySimpleInfo(null, null);
            }
            return new CategorySimpleInfo(category.getId(), category.getName());
        }
    }
}
