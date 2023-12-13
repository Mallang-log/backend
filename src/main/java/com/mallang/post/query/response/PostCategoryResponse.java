package com.mallang.post.query.response;

import com.mallang.post.domain.PostCategory;
import java.util.List;
import lombok.Builder;

@Builder
public record PostCategoryResponse(
        Long id,
        String name,
        Long parentId,
        Long prevId,
        Long nextId,
        List<PostCategoryResponse> children
) {
    public static PostCategoryResponse from(PostCategory postCategory) {
        List<PostCategoryResponse> children = postCategory.getSortedChildren()
                .stream()
                .map(PostCategoryResponse::from)
                .toList();
        PostCategory parent = postCategory.getParent();
        PostCategory prev = postCategory.getPreviousSibling();
        PostCategory next = postCategory.getNextSibling();
        return PostCategoryResponse.builder()
                .id(postCategory.getId())
                .name(postCategory.getName())
                .parentId(parent == null ? null : parent.getId())
                .prevId(prev == null ? null : prev.getId())
                .nextId(next == null ? null : next.getId())
                .children(children)
                .build();
    }
}
