package com.mallang.auth.infrastructure.oauth.github.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mallang.auth.domain.Member
import com.mallang.auth.domain.OauthId
import com.mallang.auth.domain.OauthServerType.GITHUB

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GithubMemberResponse(
        val id: String,
        val name: String,
        val avatarUrl: String
) {
    fun toMember(): Member = Member(OauthId(id, GITHUB), name, avatarUrl);
}
