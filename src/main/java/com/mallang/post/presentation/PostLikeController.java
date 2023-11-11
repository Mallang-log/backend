package com.mallang.post.presentation;

import static org.springframework.http.HttpStatus.CREATED;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.post.application.PostLikeService;
import com.mallang.post.presentation.request.CancelPostLikeRequest;
import com.mallang.post.presentation.request.ClickPostLikeRequest;
import com.mallang.post.presentation.support.OptionalPostPassword;
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
            @OptionalPostPassword String postPassword,
            @RequestBody ClickPostLikeRequest request
    ) {
        postLikeService.click(request.toCommand(memberId, postPassword));
        return ResponseEntity.status(CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> cancel(
            @Auth Long memberId,
            @OptionalPostPassword String postPassword,
            @RequestBody CancelPostLikeRequest request
    ) {
        postLikeService.cancel(request.toCommand(memberId, postPassword));
        return ResponseEntity.noContent().build();
    }
}
