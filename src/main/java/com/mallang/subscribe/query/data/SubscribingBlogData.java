package com.mallang.subscribe.query.data;

import java.time.LocalDateTime;

public record SubscribingBlogData(
        Long blogId,
        String blogName,
        Long ownerId,
        String ownerNickname,
        String ownerProfileImageUrl,
        LocalDateTime subscribeData
) {

}
