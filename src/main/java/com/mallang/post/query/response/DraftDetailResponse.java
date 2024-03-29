package com.mallang.post.query.response;

import com.mallang.post.domain.PostCategory;
import com.mallang.post.domain.PostContent;
import com.mallang.post.domain.draft.Draft;
import java.util.List;

public record DraftDetailResponse(
        Long draftId,
        String title,
        String bodyText,
        CategoryResponse category,
        TagResponses tags
) {
    public static DraftDetailResponse from(Draft draft) {
        return new DraftDetailResponse(
                draft.getId(),
                draft.getTitle(),
                draft.getBodyText(),
                CategoryResponse.from(draft.getContent()),
                TagResponses.from(draft.getContent())
        );
    }

    public record CategoryResponse(
            Long categoryId,
            String categoryName
    ) {
        private static CategoryResponse from(PostContent postContent) {
            PostCategory postCategory = postContent.getCategory();
            if (postCategory == null) {
                return new CategoryResponse(null, null);
            }
            return new CategoryResponse(postCategory.getId(), postCategory.getName());
        }
    }

    public record TagResponses(
            List<String> tagContents
    ) {
        private static TagResponses from(PostContent postContent) {
            return new TagResponses(postContent.getTags());
        }
    }
}
