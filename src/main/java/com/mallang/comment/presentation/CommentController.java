package com.mallang.comment.presentation;

import com.mallang.comment.application.CommentService;
import com.mallang.comment.presentation.request.WriteAnonymousCommentRequest;
import com.mallang.comment.presentation.request.WriteAuthenticatedCommentRequest;
import com.mallang.common.auth.Auth;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> write(
            @Auth Long memberId,
            @RequestBody WriteAuthenticatedCommentRequest request
    ) {
        Long id = commentService.write(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/comments/" + id)).build();
    }

    @PostMapping(params = "anonymous=true")
    public ResponseEntity<Void> anonymousWrite(
            @RequestBody WriteAnonymousCommentRequest request
    ) {
        Long id = commentService.write(request.toCommand());
        return ResponseEntity.created(URI.create("/comments/" + id)).build();
    }
}
