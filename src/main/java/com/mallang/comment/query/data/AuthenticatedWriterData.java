package com.mallang.comment.query.data;

import com.mallang.member.domain.Member;
import lombok.Builder;

@Builder
public record AuthenticatedWriterData(
        Long memberId,
        String nickname,
        String profileImageUrl
) implements CommentWriterData {

    public static AuthenticatedWriterData from(Member member) {
        return AuthenticatedWriterData.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
