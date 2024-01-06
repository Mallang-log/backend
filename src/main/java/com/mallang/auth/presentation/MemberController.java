package com.mallang.auth.presentation;

import static org.springframework.http.HttpStatus.OK;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.auth.query.MemberQueryService;
import com.mallang.auth.query.response.MemberResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        request.getSession().invalidate();
        for (Cookie cookie : request.getCookies()) {
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
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
