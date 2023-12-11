package com.mallang.post.query.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mallang.auth.domain.Member;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostCategory;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PostDetailResponse(
        Long postId,
        Long blogId,
        String blogName,
        String title,
        String bodyText,
        @Nullable String postThumbnailImageName,
        Visibility visibility,
        boolean isProtected,
        @JsonIgnore String password,
        int likeCount,
        boolean isLiked,
        LocalDateTime createdDate,
        WriterResponse writer,
        CategoryResponse category,
        TagResponses tags
) {
    public static PostDetailResponse from(Post post) {
        return withLiked(post, false);
    }

    public static PostDetailResponse withLiked(Post post, boolean isLiked) {
        return PostDetailResponse.builder()
                .postId(post.getId().getPostId())
                .blogId(post.getId().getBlogId())
                .blogName(post.getBlog().getName())
                .title(post.getTitle())
                .bodyText(post.getBodyText())
                .postThumbnailImageName(post.getPostThumbnailImageName())
                .visibility(post.getVisibilityPolish().getVisibility())
                .password(post.getVisibilityPolish().getPassword())
                .likeCount(post.getLikeCount())
                .isLiked(isLiked)
                .createdDate(post.getCreatedDate())
                .writer(WriterResponse.from(post))
                .category(CategoryResponse.from(post))
                .tags(TagResponses.from(post))
                .build();
    }

    public static PostDetailResponse protectedPost(Post post) {
        return new PostDetailResponse(
                post.getId().getPostId(),
                post.getId().getBlogId(),
                post.getBlog().getName(),
                post.getTitle(),
                "보호되어 있는 글입니다. 내용을 보시려면 비밀번호를 입력하세요.",
                "",
                post.getVisibilityPolish().getVisibility(),
                true,
                "",
                0,
                false,
                post.getCreatedDate(),
                WriterResponse.from(post),
                CategoryResponse.from(post),
                null
        );
    }

    public record WriterResponse(
            Long writerId,
            String writerNickname,
            String writerProfileImageUrl
    ) {
        private static WriterResponse from(Post post) {
            Member member = post.getWriter();
            return new WriterResponse(
                    member.getId(),
                    member.getNickname(),
                    member.getProfileImageUrl()
            );
        }
    }

    public record CategoryResponse(
            Long categoryId,
            String categoryName
    ) {
        private static CategoryResponse from(Post post) {
            PostCategory postCategory = post.getCategory();
            if (postCategory == null) {
                return new CategoryResponse(null, null);
            }
            return new CategoryResponse(postCategory.getId(), postCategory.getName());
        }
    }

    public record TagResponses(
            List<String> tagContents
    ) {
        private static TagResponses from(Post post) {
            return new TagResponses(post.getTags());
        }
    }
}
