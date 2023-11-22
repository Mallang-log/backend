package com.mallang.subscribe.query.response;

import java.time.LocalDateTime;

public record SubscriberResponse(
        Long subscriberId,
        String subscriberNickname,
        String subscriberProfileImageUrl,
        LocalDateTime subscribeData
) {
}
