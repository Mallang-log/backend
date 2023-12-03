package com.mallang.post.query.response;

import com.mallang.auth.domain.Member;
import com.mallang.category.domain.Category;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.mallang.post.domain.star.PostStar;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record StaredPostResponse(
        Long starId,
        LocalDateTime staredData,
        Long postId,
        String blogName,
        String title,
        String intro,
        String bodyText,
        @Nullable String postThumbnailImageName,
        Visibility visibility,
        LocalDateTime postCreatedDate,
        WriterResponse writer,
        CategoryResponse category,
        TagResponses tags
) {
    public static StaredPostResponse from(PostStar postStar) {
        Post post = postStar.getPost();
        return StaredPostResponse.builder()
                .starId(postStar.getId())
                .staredData(postStar.getCreatedDate())
                .postId(post.getId().getPostId())
                .blogName(post.getBlog().getName())
                .title(post.getTitle())
                .bodyText(post.getBodyText())
                .intro(post.getPostIntro())
                .postThumbnailImageName(post.getPostThumbnailImageName())
                .visibility(post.getVisibilityPolish().getVisibility())
                .postCreatedDate(post.getCreatedDate())
                .writer(WriterResponse.from(post))
                .category(CategoryResponse.from(post))
                .tags(TagResponses.from(post))
                .build();
    }

    public static StaredPostResponse protectedPost(PostStar postStar) {
        Post post = postStar.getPost();
        return new StaredPostResponse(
                postStar.getId(),
                postStar.getCreatedDate(),
                post.getId().getPostId(),
                post.getBlog().getName(),
                post.getTitle(),
                "보호되어 있는 글입니다.", "보호되어 있는 글입니다.",
                "",
                post.getVisibilityPolish().getVisibility(),
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
            Category category = post.getCategory();
            if (category == null) {
                return new CategoryResponse(null, null);
            }
            return new CategoryResponse(category.getId(), category.getName());
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
