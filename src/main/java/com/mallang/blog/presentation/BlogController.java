package com.mallang.blog.presentation;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.blog.application.BlogService;
import com.mallang.blog.presentation.request.OpenBlogRequest;
import com.mallang.blog.query.BlogQueryService;
import com.mallang.blog.query.response.BlogResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/blogs")
@RestController
public class BlogController {

    private final BlogService blogService;
    private final BlogQueryService blogQueryService;

    @PostMapping
    public ResponseEntity<Void> open(
            @Auth Long memberId,
            @RequestBody OpenBlogRequest request
    ) {
        Long blogId = blogService.open(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/blogs/" + blogId)).build();
    }

    @GetMapping
    public ResponseEntity<BlogResponse> findByName(
            @RequestParam("blogName") String blogName
    ) {
        return ResponseEntity.ok(blogQueryService.findByName(blogName));
    }

    @GetMapping("/my")
    public ResponseEntity<BlogResponse> findMyBlog(
            @Auth Long memberId
    ) {
        return ResponseEntity.ok(blogQueryService.findByOwnerId(memberId));
    }
}
