package com.mallang.comment.presentation;

import static com.mallang.post.presentation.support.PostPresentationConstant.POST_PASSWORD_COOKIE;

import com.mallang.auth.presentation.support.OptionalAuth;
import com.mallang.comment.application.UnAuthCommentService;
import com.mallang.comment.presentation.request.DeleteUnAuthCommentRequest;
import com.mallang.comment.presentation.request.UpdateUnAuthCommentRequest;
import com.mallang.comment.presentation.request.WriteUnAuthCommentRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(path = "/comments", params = "unauthenticated=true")
@RestController
public class UnAuthCommentController {

    private final UnAuthCommentService unAuthCommentService;

    @PostMapping
    public ResponseEntity<Void> unAuthenticatedWrite(
            @CookieValue(name = POST_PASSWORD_COOKIE, required = false) String postPassword,
            @RequestBody WriteUnAuthCommentRequest request
    ) {
        Long id = unAuthCommentService.write(request.toCommand(postPassword));
        return ResponseEntity.created(URI.create("/comments/" + id)).build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("id") Long commentId,
            @CookieValue(name = POST_PASSWORD_COOKIE, required = false) String postPassword,
            @RequestBody UpdateUnAuthCommentRequest request
    ) {
        unAuthCommentService.update(request.toCommand(commentId, postPassword));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long commentId,
            @OptionalAuth Long memberId,
            @CookieValue(name = POST_PASSWORD_COOKIE, required = false) String postPassword,
            @RequestBody DeleteUnAuthCommentRequest request
    ) {
        unAuthCommentService.delete(request.toCommand(memberId, commentId, postPassword));
        return ResponseEntity.ok().build();
    }
}
