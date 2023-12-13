package com.mallang.post.query.response;

import com.mallang.post.domain.star.StarGroup;
import java.util.List;

public record StarGroupListResponse(
        Long id,
        String name,
        Long parentId,
        Long prevId,
        Long nextId,
        List<StarGroupListResponse> children
) {

    public static StarGroupListResponse from(StarGroup starGroup) {
        List<StarGroupListResponse> children = starGroup.getSortedChildren()
                .stream()
                .map(StarGroupListResponse::from)
                .toList();
        StarGroup parent = starGroup.getParent();
        StarGroup prev = starGroup.getPreviousSibling();
        StarGroup next = starGroup.getNextSibling();
        return new StarGroupListResponse(
                starGroup.getId(),
                starGroup.getName(),
                parent == null ? null : parent.getId(),
                prev == null ? null : prev.getId(),
                next == null ? null : next.getId(),
                children
        );
    }
}
