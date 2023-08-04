package com.mallang.post.application.query;

import com.mallang.post.domain.Post;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PostSimpleResponse(
        Long id,
        Long writerId,
        String writerNickname,
        String writerProfileImageUrl,
        String title,
        String content,
        LocalDateTime createdDate
) {

    public static PostSimpleResponse from(Post post) {
        return PostSimpleResponse.builder()
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
