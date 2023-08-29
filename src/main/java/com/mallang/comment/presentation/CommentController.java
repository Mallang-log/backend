package com.mallang.comment.presentation;

import com.mallang.comment.application.CommentService;
import com.mallang.comment.application.command.DeleteCommentCommand;
import com.mallang.comment.domain.writer.AuthenticatedWriterCredential;
import com.mallang.comment.presentation.request.DeleteAnonymousCommentRequest;
import com.mallang.comment.presentation.request.UpdateAnonymousCommentRequest;
import com.mallang.comment.presentation.request.UpdateAuthenticatedCommentRequest;
import com.mallang.comment.presentation.request.WriteAnonymousCommentRequest;
import com.mallang.comment.presentation.request.WriteAuthenticatedCommentRequest;
import com.mallang.comment.query.CommentQueryService;
import com.mallang.comment.query.data.CommentData;
import com.mallang.common.auth.Auth;
import com.mallang.common.auth.OptionalAuth;
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

    private final CommentService commentService;
    private final CommentQueryService commentQueryService;

    @PostMapping
    public ResponseEntity<Void> write(
            @Auth Long memberId,
            @RequestBody WriteAuthenticatedCommentRequest request
    ) {
        Long id = commentService.write(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/comments/" + id)).build();
    }

    @PostMapping(params = "unauthenticated=true")
    public ResponseEntity<Void> anonymousWrite(
            @RequestBody WriteAnonymousCommentRequest request
    ) {
        Long id = commentService.write(request.toCommand());
        return ResponseEntity.created(URI.create("/comments/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("id") Long commentId,
            @Auth Long memberId,
            @RequestBody UpdateAuthenticatedCommentRequest request
    ) {
        commentService.update(request.toCommand(commentId, memberId));
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{id}", params = "unauthenticated=true")
    public ResponseEntity<Void> update(
            @PathVariable("id") Long commentId,
            @RequestBody UpdateAnonymousCommentRequest request
    ) {
        commentService.update(request.toCommand(commentId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long commentId,
            @Auth Long memberId
    ) {
        DeleteCommentCommand command = DeleteCommentCommand.builder()
                .commentId(commentId)
                .credential(new AuthenticatedWriterCredential(memberId))
                .build();
        commentService.delete(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}", params = "unauthenticated=true")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long commentId,
            @RequestBody DeleteAnonymousCommentRequest request
    ) {
        commentService.delete(request.toCommand(commentId));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CommentData>> findAll(
            @Nullable @OptionalAuth Long memberId,
            @RequestParam(value = "postId", required = true) Long postId
    ) {
        List<CommentData> result = commentQueryService.findAllByPostId(postId, memberId);
        return ResponseEntity.ok(result);
    }
}
