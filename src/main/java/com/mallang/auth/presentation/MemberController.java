package com.mallang.auth.presentation;

import static com.mallang.auth.presentation.support.AuthConstant.MEMBER_ID;
import static org.springframework.http.HttpStatus.OK;

import com.mallang.auth.application.BasicAuthService;
import com.mallang.auth.presentation.request.BasicLoginRequest;
import com.mallang.auth.presentation.request.BasicSignupRequest;
import com.mallang.auth.presentation.support.Auth;
import com.mallang.auth.query.MemberQueryService;
import com.mallang.auth.query.response.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final BasicAuthService basicAuthService;
    private final MemberQueryService memberQueryService;

    @Value("${auth.session.ttl}")
    private Integer authSessionTTL;

    @PostMapping
    public ResponseEntity<Void> signup(
            @Valid @RequestBody BasicSignupRequest request
    ) {
        Long id = basicAuthService.signup(request.toCommand());
        return ResponseEntity.created(URI.create("/members/" + id)).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody BasicLoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        Long memberId = basicAuthService.login(request.username(), request.password());
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(MEMBER_ID, memberId);
        session.setMaxInactiveInterval(authSessionTTL);
        return ResponseEntity.status(OK).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> findProfile(
            @PathVariable("id") Long memberId
    ) {
        return ResponseEntity.ok(memberQueryService.findProfile(memberId));
    }

    @GetMapping("/my")
    public ResponseEntity<MemberResponse> findMyProfile(
            @Auth Long memberId
    ) {
        return ResponseEntity.ok(memberQueryService.findProfile(memberId));
    }
}
