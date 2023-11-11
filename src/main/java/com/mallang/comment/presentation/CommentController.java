package com.mallang.comment.presentation;

import com.mallang.comment.application.AuthenticatedCommentService;
import com.mallang.comment.application.UnAuthenticatedCommentService;
import com.mallang.comment.application.command.DeleteAuthenticatedCommentCommand;
import com.mallang.comment.presentation.request.DeleteUnAuthenticatedCommentRequest;
import com.mallang.comment.presentation.request.UpdateAuthenticatedCommentRequest;
import com.mallang.comment.presentation.request.UpdateUnAuthenticatedCommentRequest;
import com.mallang.comment.presentation.request.WriteAnonymousCommentRequest;
import com.mallang.comment.presentation.request.WriteAuthenticatedCommentRequest;
import com.mallang.comment.query.CommentQueryService;
import com.mallang.comment.query.data.CommentData;
import com.mallang.common.auth.Auth;
import com.mallang.common.auth.OptionalAuth;
import com.mallang.post.presentation.support.OptionalPostPassword;
import jakarta.annotation.Nullable;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    private final AuthenticatedCommentService authenticatedCommentService;
    private final UnAuthenticatedCommentService unAuthenticatedCommentService;
    private final CommentQueryService commentQueryService;

    @PostMapping
    public ResponseEntity<Void> write(
            @Auth Long memberId,
            @OptionalPostPassword String postPassword,
            @RequestBody WriteAuthenticatedCommentRequest request
    ) {
        Long id = authenticatedCommentService.write(request.toCommand(memberId, postPassword));
        return ResponseEntity.created(URI.create("/comments/" + id)).build();
    }

    @PostMapping(params = "unauthenticated=true")
    public ResponseEntity<Void> unAuthenticatedWrite(
            @OptionalPostPassword String postPassword,
            @RequestBody WriteAnonymousCommentRequest request
    ) {
        Long id = unAuthenticatedCommentService.write(request.toCommand(postPassword));
        return ResponseEntity.created(URI.create("/comments/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @OptionalPostPassword String postPassword,
            @PathVariable("id") Long commentId,
            @Auth Long memberId,
            @RequestBody UpdateAuthenticatedCommentRequest request
    ) {
        authenticatedCommentService.update(request.toCommand(commentId, memberId, postPassword));
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{id}", params = "unauthenticated=true")
    public ResponseEntity<Void> update(
            @OptionalPostPassword String postPassword,
            @PathVariable("id") Long commentId,
            @RequestBody UpdateUnAuthenticatedCommentRequest request
    ) {
        unAuthenticatedCommentService.update(request.toCommand(commentId, postPassword));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @OptionalPostPassword String postPassword,
            @PathVariable("id") Long commentId,
            @Auth Long memberId
    ) {
        DeleteAuthenticatedCommentCommand command = DeleteAuthenticatedCommentCommand.builder()
                .postPassword(postPassword)
                .memberId(memberId)
                .commentId(commentId)
                .build();
        authenticatedCommentService.delete(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}", params = "unauthenticated=true")
    public ResponseEntity<Void> delete(
            @OptionalPostPassword String postPassword,
            @OptionalAuth Long memberId,
            @PathVariable("id") Long commentId,
            @RequestBody DeleteUnAuthenticatedCommentRequest request
    ) {
        unAuthenticatedCommentService.delete(request.toCommand(memberId, commentId, postPassword));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CommentData>> findAll(
            @OptionalPostPassword String postPassword,
            @Nullable @OptionalAuth Long memberId,
            @RequestParam(value = "postId", required = true) Long postId
    ) {
        List<CommentData> result = commentQueryService.findAllByPostId(postId, memberId, postPassword);
        return ResponseEntity.ok(result);
    }
}
