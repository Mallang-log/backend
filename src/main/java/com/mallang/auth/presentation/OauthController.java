package com.mallang.auth.presentation;

import static com.mallang.common.auth.AuthConstant.JSESSION_ID;

import com.mallang.auth.application.OauthService;
import com.mallang.auth.presentation.response.SessionResponse;
import com.mallang.member.domain.OauthServerType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @Value("${auth.session.ttl}")
    private Integer authSessionTtl;

    @SneakyThrows
    @GetMapping("/{oauthServerType}")
    ResponseEntity<Void> redirectAuthCodeRequestUrl(
            @PathVariable OauthServerType oauthServerType,
            HttpServletResponse response
    ) {
        String redirectUrl = oauthService.getAuthCodeRequestUrl(oauthServerType);
        response.sendRedirect(redirectUrl);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/{oauthServerType}")
    ResponseEntity<SessionResponse> login(
            @PathVariable OauthServerType oauthServerType,
            @RequestParam("code") String code,
            HttpServletRequest request
    ) {
        Long memberId = oauthService.login(oauthServerType, code);
        HttpSession session = request.getSession(true);
        session.setAttribute(JSESSION_ID, memberId);
        session.setMaxInactiveInterval(authSessionTtl);
        return ResponseEntity.ok(new SessionResponse(session.getId()));
    }
}
