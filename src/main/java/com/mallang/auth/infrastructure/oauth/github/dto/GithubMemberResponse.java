package com.mallang.auth.infrastructure.oauth.github.dto;


import static com.mallang.auth.domain.OauthId.OauthServerType.GITHUB;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mallang.auth.domain.OauthId;
import com.mallang.auth.domain.OauthMember;

@JsonNaming(SnakeCaseStrategy.class)
public record GithubMemberResponse(
        String id,
        String name,
        String avatarUrl
) {

    public OauthMember toMember() {
        return OauthMember.builder()
                .oauthId(new OauthId(id, GITHUB))
                .nickname(name)
                .profileImageUrl(avatarUrl)
                .build();
    }
}
