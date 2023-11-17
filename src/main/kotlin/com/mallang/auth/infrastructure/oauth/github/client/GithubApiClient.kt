package com.mallang.auth.infrastructure.oauth.github.client

import com.mallang.auth.infrastructure.oauth.github.dto.GithubMemberResponse
import com.mallang.auth.infrastructure.oauth.github.dto.GithubToken
import com.mallang.auth.infrastructure.oauth.github.dto.GithubTokenRequest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

interface GithubApiClient {

    @PostExchange(url = "https://github.com/login/oauth/access_token", accept = [APPLICATION_JSON_VALUE])
    fun fetchToken(@RequestBody request: GithubTokenRequest): GithubToken

    @GetExchange("https://api.github.com/user")
    fun fetchMember(@RequestHeader(name = AUTHORIZATION) bearerToken: String): GithubMemberResponse
}
