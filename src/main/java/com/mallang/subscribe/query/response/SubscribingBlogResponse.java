package com.mallang.subscribe.query.response;

import java.time.LocalDateTime;

public record SubscribingBlogResponse(
        Long blogId,
        String blogName,
        Long ownerId,
        String ownerNickname,
        String ownerProfileImageUrl,
        LocalDateTime subscribeData
) {
}
