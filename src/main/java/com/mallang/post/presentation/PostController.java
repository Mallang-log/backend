package com.mallang.post.presentation;

import com.mallang.common.auth.Auth;
import com.mallang.post.application.PostQueryService;
import com.mallang.post.application.PostService;
import com.mallang.post.application.query.PostDetailResponse;
import com.mallang.post.application.query.PostSimpleResponse;
import com.mallang.post.presentation.request.CreatePostRequest;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostService postService;
    private final PostQueryService postQueryService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Auth Long memberId,
            @RequestBody CreatePostRequest request
    ) {
        Long id = postService.create(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/posts/" + id)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(postQueryService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<PostSimpleResponse>> findAll() {
        return ResponseEntity.ok(postQueryService.findAll());
    }
}
