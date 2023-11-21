package com.mallang.blog.query.data;

import com.mallang.blog.domain.About;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AboutResponse(
        Long id,
        String content,
        LocalDateTime createdDate
) {

    public static AboutResponse from(About about) {
        return AboutResponse.builder()
                .id(about.getId())
                .content(about.getContent())
                .createdDate(about.getCreatedDate())
                .build();
    }
}
