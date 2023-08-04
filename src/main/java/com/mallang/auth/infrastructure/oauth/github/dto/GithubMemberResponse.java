package com.mallang.auth.infrastructure.oauth.github.dto;

import static com.mallang.member.domain.OauthServerType.GITHUB;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.OauthId;

@JsonNaming(SnakeCaseStrategy.class)
public record GithubMemberResponse(
        String id,
        String name,
        String avatarUrl
) {

    public Member toMember() {
        return Member.builder()
                .oauthId(new OauthId(id, GITHUB))
                .nickname(name)
                .profileImageUrl(avatarUrl)
                .build();
    }
}
