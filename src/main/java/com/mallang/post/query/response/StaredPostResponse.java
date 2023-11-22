package com.mallang.post.query.response;

import com.mallang.auth.domain.Member;
import com.mallang.category.domain.Category;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.star.PostStar;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record StaredPostResponse(
        Long starId,
        LocalDateTime staredData,
        Long postId,
        String title,
        String content,
        String intro,
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
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .intro(post.getPostIntro())
                .postThumbnailImageName(post.getPostThumbnailImageName())
                .visibility(post.getVisibilityPolish().getVisibility())
                .postCreatedDate(post.getCreatedDate())
                .writer(WriterResponse.from(post))
                .category(CategoryResponse.from(post))
                .tags(TagResponses.from(post))
                .build();
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
