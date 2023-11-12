package com.mallang.auth.presentation;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.auth.query.MemberQueryService;
import com.mallang.auth.query.data.MemberProfileData;
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

    @GetMapping("/{id}")
    public ResponseEntity<MemberProfileData> findProfile(
            @PathVariable("id") Long memberId
    ) {
        return ResponseEntity.ok(memberQueryService.findProfile(memberId));
    }

    @GetMapping("/my")
    public ResponseEntity<MemberProfileData> findMyProfile(
            @Auth Long memberId
    ) {
        return ResponseEntity.ok(memberQueryService.findProfile(memberId));
    }
}