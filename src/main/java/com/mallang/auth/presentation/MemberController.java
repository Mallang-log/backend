package com.mallang.auth.presentation;

import com.mallang.auth.application.BasicAuthService;
import com.mallang.auth.presentation.request.BasicSignupRequest;
import com.mallang.auth.presentation.support.Auth;
import com.mallang.auth.query.MemberQueryService;
import com.mallang.auth.query.response.MemberResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<Void> signup(
            @Valid @RequestBody BasicSignupRequest request
    ) {
        Long id = basicAuthService.signup(request.toCommand());
        return ResponseEntity.created(URI.create("/members/" + id)).build();
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
