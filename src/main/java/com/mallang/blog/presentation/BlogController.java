package com.mallang.blog.presentation;

import com.mallang.blog.application.BlogService;
import com.mallang.blog.presentation.request.OpenBlogRequest;
import com.mallang.common.auth.Auth;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/blogs")
@RestController
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    public ResponseEntity<Void> open(
            @Auth Long memberId,
            @RequestBody OpenBlogRequest request
    ) {
        Long blogId = blogService.open(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/blogs/" + blogId)).build();
    }
}
