package com.mallang.subscribe.query.data;

import java.time.LocalDateTime;

public record SubscriberData(
        Long subscriberId,
        String subscriberNickname,
        String subscriberProfileImageUrl,
        LocalDateTime subscribeData
) {

}
