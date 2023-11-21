package com.mallang.auth.query.response;

import com.mallang.auth.domain.Member;

public record MemberResponse(
        Long id,
        String nickname,
        String profileImageUrl
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl()
        );
    }
}
