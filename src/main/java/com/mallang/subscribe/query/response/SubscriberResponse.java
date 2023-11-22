package com.mallang.subscribe.query.response;

import com.mallang.auth.domain.Member;
import com.mallang.subscribe.domain.BlogSubscribe;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record SubscriberResponse(
        Long subscriberId,
        String subscriberNickname,
        String subscriberProfileImageUrl,
        LocalDateTime subscribeData
) {
    public static SubscriberResponse from(BlogSubscribe blogSubscribe) {
        Member subscriber = blogSubscribe.getSubscriber();
        return SubscriberResponse.builder()
                .subscriberId(subscriber.getId())
                .subscriberNickname(subscriber.getNickname())
                .subscriberProfileImageUrl(subscriber.getProfileImageUrl())
                .subscribeData(subscriber.getCreatedDate())
                .build();
    }
}
