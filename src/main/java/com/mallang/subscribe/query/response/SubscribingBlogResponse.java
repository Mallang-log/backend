package com.mallang.subscribe.query.response;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.subscribe.domain.BlogSubscribe;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record SubscribingBlogResponse(
        Long blogId,
        String blogName,
        Long subscriberId,
        String subscriberNickname,
        String subscriberProfileImageUrl,
        LocalDateTime subscribeData
) {
    public static SubscribingBlogResponse from(BlogSubscribe blogSubscribe) {
        Blog blog = blogSubscribe.getBlog();
        Member subscriber = blogSubscribe.getSubscriber();
        return SubscribingBlogResponse.builder()
                .blogId(blog.getId())
                .blogName(blog.getName())
                .subscriberId(subscriber.getId())
                .subscriberNickname(subscriber.getNickname())
                .subscriberProfileImageUrl(subscriber.getProfileImageUrl())
                .subscribeData(blogSubscribe.getCreatedDate())
                .build();
    }
}
