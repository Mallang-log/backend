package com.mallang.post.query.data;

import com.mallang.auth.domain.Member;
import com.mallang.category.domain.Category;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.Tag;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PostSimpleData(
        Long id,
        String title,
        String content,
        String intro,
        Visibility visibility,
        int likeCount,
        LocalDateTime createdDate,
        WriterSimpleInfo writerInfo,
        CategorySimpleInfo categoryInfo,
        TagSimpleInfos tagSimpleInfos
) {

    public static PostSimpleData from(Post post) {
        return PostSimpleData.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .intro(post.getPostIntro())
                .visibility(post.getVisibilityPolish().getVisibility())
                .likeCount(post.getLikeCount())
                .createdDate(post.getCreatedDate())
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
            return new TagSimpleInfos(post.getTags().stream()
                    .map(Tag::getContent)
                    .toList());
        }
    }
}
