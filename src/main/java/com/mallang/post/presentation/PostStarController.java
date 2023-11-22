package com.mallang.post.presentation;

import static com.mallang.post.presentation.support.PostPresentationConstant.POST_PASSWORD_COOKIE;
import static org.springframework.http.HttpStatus.CREATED;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.auth.presentation.support.OptionalAuth;
import com.mallang.common.presentation.PageResponse;
import com.mallang.post.application.PostStarService;
import com.mallang.post.presentation.request.CancelPostStarRequest;
import com.mallang.post.presentation.request.StarPostRequest;
import com.mallang.post.query.PostStarQueryService;
import com.mallang.post.query.response.StaredPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/post-stars")
@RestController
public class PostStarController {

    private final PostStarService postStarService;
    private final PostStarQueryService postStarQueryService;

    @PostMapping
    public ResponseEntity<Void> click(
            @Auth Long memberId,
            @CookieValue(name = POST_PASSWORD_COOKIE, required = false) String postPassword,
            @RequestBody StarPostRequest request
    ) {
        postStarService.star(request.toCommand(memberId, postPassword));
        return ResponseEntity.status(CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> cancel(
            @Auth Long memberId,
            @RequestBody CancelPostStarRequest request
    ) {
        postStarService.cancel(request.toCommand(memberId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<StaredPostResponse>> findAllByMemberId(
            @OptionalAuth Long requesterId,
            @RequestParam("memberId") Long targetMemberId,
            @PageableDefault(size = 9) Pageable pageable
    ) {
        return ResponseEntity.ok(
                PageResponse.from(postStarQueryService.findAllByMemberId(targetMemberId, requesterId, pageable)));
    }
}
