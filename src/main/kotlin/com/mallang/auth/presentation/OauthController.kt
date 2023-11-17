package com.mallang.auth.presentation

import com.mallang.auth.application.OauthService
import com.mallang.auth.domain.OauthServerType
import com.mallang.auth.presentation.support.MEMBER_ID
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/oauth")
@RestController
class OauthController(
        private val oauthService: OauthService,
        @Value("\${auth.session.ttl}")
        private val authSessionTtl: Int
) {

    @GetMapping("/{oauthServerType}")
    fun redirectAuthCodeRequestUrl(
            @PathVariable oauthServerType: OauthServerType,
            response: HttpServletResponse
    ): ResponseEntity<Unit> {
        val redirectUrl = oauthService.getAuthCodeRequestUrl(oauthServerType)
        response.sendRedirect(redirectUrl)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/login/{oauthServerType}")
    fun login(
            @PathVariable oauthServerType: OauthServerType,
            @RequestParam("code") code: String,
            request: HttpServletRequest
    ): ResponseEntity<Unit> {
        val memberId = oauthService.login(oauthServerType, code)
        with(request.getSession(true)) {
            setAttribute(MEMBER_ID, memberId)
            maxInactiveInterval = authSessionTtl
        }
        return ResponseEntity.status(OK).build()
    }
}
