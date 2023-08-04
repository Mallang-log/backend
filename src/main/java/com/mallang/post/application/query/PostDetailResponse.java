package com.mallang.post.application.query;

import com.mallang.post.domain.Post;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PostDetailResponse(
        Long id,
        Long writerId,
        String writerNickname,
        String writerProfileImageUrl,
        String title,
        String content,
        LocalDateTime createdDate
) {

    public static PostDetailResponse from(Post post) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .writerId(post.getMember().getId())
                .writerNickname(post.getMember().getNickname())
                .writerProfileImageUrl(post.getMember().getProfileImageUrl())
                .title(post.getTitle())
                .content(post.getContent())
                .createdDate(post.getCreatedDate())
                .build();
    }
}
