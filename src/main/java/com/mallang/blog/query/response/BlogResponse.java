package com.mallang.blog.query.response;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import lombok.Builder;

@Builder
public record BlogResponse(
        Long id,
        String name,
        OwnerResponse owner
) {

    public static BlogResponse from(Blog blog) {
        return BlogResponse.builder()
                .id(blog.getId())
                .name(blog.getName())
                .owner(OwnerResponse.from(blog.getOwner()))
                .build();
    }

    @Builder
    public record OwnerResponse(
            Long memberId,
            String nickname,
            String profileImageUrl
    ) {
        public static OwnerResponse from(Member owner) {
            return OwnerResponse.builder()
                    .memberId(owner.getId())
                    .nickname(owner.getNickname())
                    .profileImageUrl(owner.getProfileImageUrl())
                    .build();
        }
    }
}
