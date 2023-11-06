package com.mallang.post.presentation;

import static org.springframework.http.HttpStatus.CREATED;

import com.mallang.common.auth.Auth;
import com.mallang.post.application.PostLikeService;
import com.mallang.post.presentation.request.CancelPostLikeRequest;
import com.mallang.post.presentation.request.ClickPostLikeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/post-likes")
@RestController
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping
    public ResponseEntity<Void> click(
            @Auth Long memberId,
            @RequestBody ClickPostLikeRequest request
    ) {
        postLikeService.click(request.toCommand(memberId));
        return ResponseEntity.status(CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> click(
            @Auth Long memberId,
            @RequestBody CancelPostLikeRequest request
    ) {
        postLikeService.cancel(request.toCommand(memberId));
        return ResponseEntity.noContent().build();
    }
}
