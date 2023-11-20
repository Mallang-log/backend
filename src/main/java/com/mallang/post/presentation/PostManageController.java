package com.mallang.post.presentation;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.post.application.PostService;
import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.post.presentation.request.DeletePostRequest;
import com.mallang.post.presentation.request.UpdatePostRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/manage/posts")
@RestController
public class PostManageController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Auth Long memberId,
            @RequestBody CreatePostRequest request
    ) {
        Long id = postService.create(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/posts/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable(name = "id") Long postId,
            @Auth Long memberId,
            @RequestBody UpdatePostRequest request
    ) {
        postService.update(request.toCommand(memberId, postId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @Auth Long memberId,
            @RequestBody DeletePostRequest request
    ) {
        postService.delete(request.toCommand(memberId));
        return ResponseEntity.noContent().build();
    }
}
