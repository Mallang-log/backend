package com.mallang.post.query.data;

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
public record StaredPostData(
        Long starId,
        LocalDateTime staredData,
        Long postId,
        String title,
        String content,
        String intro,
        @Nullable String postThumbnailImageName,
        Visibility visibility,
        LocalDateTime postCreatedDate,
        WriterSimpleInfo writerInfo,
        CategorySimpleInfo categoryInfo,
        TagSimpleInfos tagSimpleInfos
) {

    public static StaredPostData from(PostStar postStar) {
        Post post = postStar.getPost();
        return StaredPostData.builder()
                .starId(postStar.getId())
                .staredData(postStar.getCreatedDate())
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .intro(post.getPostIntro())
                .postThumbnailImageName(post.getPostThumbnailImageName())
                .visibility(post.getVisibilityPolish().getVisibility())
                .postCreatedDate(post.getCreatedDate())
                .writerInfo(WriterSimpleInfo.from(post))
                .categoryInfo(CategorySimpleInfo.from(post))
                .tagSimpleInfos(TagSimpleInfos.from(post))
                .build();
    }

    public record WriterSimpleInfo(
            Long writerId,
            String writerNickname,
            String writerProfileImageUrl
    ) {
        private static WriterSimpleInfo from(Post post) {
            Member member = post.getWriter();
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

    public record TagSimpleInfos(
            List<String> tagContents
    ) {
        private static TagSimpleInfos from(Post post) {
            return new TagSimpleInfos(post.getTags());
        }
    }
}
