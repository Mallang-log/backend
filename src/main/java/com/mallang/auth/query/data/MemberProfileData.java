package com.mallang.auth.query.data;

import com.mallang.auth.domain.Member;

public record MemberProfileData(
        Long id,
        String nickname,
        String profileImageUrl
) {
    public static MemberProfileData from(Member member) {
        return new MemberProfileData(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl()
        );
    }
}
