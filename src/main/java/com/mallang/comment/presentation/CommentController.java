package com.mallang.comment.presentation;

import static com.mallang.post.presentation.support.PostPresentationConstant.POST_PASSWORD_COOKIE;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.auth.presentation.support.OptionalAuth;
import com.mallang.comment.application.AuthCommentService;
import com.mallang.comment.application.command.DeleteAuthCommentCommand;
import com.mallang.comment.presentation.request.UpdateAuthCommentRequest;
import com.mallang.comment.presentation.request.WriteAuthCommentRequest;
import com.mallang.comment.query.CommentQueryService;
import com.mallang.comment.query.response.CommentResponse;
import jakarta.annotation.Nullable;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/comments")
@RestController
public class CommentController {

    private final AuthCommentService authCommentService;
    private final CommentQueryService commentQueryService;

    @PostMapping
    public ResponseEntity<Void> write(
            @Auth Long memberId,
            @CookieValue(name = POST_PASSWORD_COOKIE, required = false) String postPassword,
            @RequestBody WriteAuthCommentRequest request
    ) {
        Long id = authCommentService.write(request.toCommand(memberId, postPassword));
        return ResponseEntity.created(URI.create("/comments/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("id") Long commentId,
            @Auth Long memberId,
            @CookieValue(name = POST_PASSWORD_COOKIE, required = false) String postPassword,
            @RequestBody UpdateAuthCommentRequest request
    ) {
        authCommentService.update(request.toCommand(commentId, memberId, postPassword));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long commentId,
            @Auth Long memberId,
            @CookieValue(name = POST_PASSWORD_COOKIE, required = false) String postPassword
    ) {
        DeleteAuthCommentCommand command = DeleteAuthCommentCommand.builder()
                .postPassword(postPassword)
                .memberId(memberId)
                .commentId(commentId)
                .build();
        authCommentService.delete(command);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> findAll(
            @CookieValue(name = POST_PASSWORD_COOKIE, required = false) String postPassword,
            @Nullable @OptionalAuth Long memberId,
            @RequestParam(value = "postId", required = true) Long postId,
            @RequestParam(value = "blogName", required = true) String blogName
    ) {
        List<CommentResponse> result = commentQueryService.findAllByPost(postId, blogName, memberId, postPassword);
        return ResponseEntity.ok(result);
    }
}
