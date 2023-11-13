package com.mallang.post.application.command;

import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

@Builder
public record UpdatePostCommand(
        Long memberId,
        Long postId,
        String title,
        String content,
        @Nullable String postThumbnailImageName,
        String intro,
        Visibility visibility,
        String password,
        @Nullable Long categoryId,
        List<String> tags
) {
}
