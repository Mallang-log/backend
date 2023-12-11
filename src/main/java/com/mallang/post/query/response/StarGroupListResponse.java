package com.mallang.post.query.response;

import com.mallang.post.domain.star.StarGroup;
import java.util.List;

public record StarGroupListResponse(
        Long id,
        String name,
        List<StarGroupListResponse> children
) {

    public static StarGroupListResponse from(StarGroup starGroup) {
        List<StarGroupListResponse> children = starGroup.getSortedChildren()
                .stream()
                .map(StarGroupListResponse::from)
                .toList();
        return new StarGroupListResponse(
                starGroup.getId(),
                starGroup.getName(),
                children
        );
    }
}
