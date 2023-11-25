package com.mallang.blog.presentation;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.blog.application.BlogSubscribeService;
import com.mallang.blog.presentation.request.BlogSubscribeRequest;
import com.mallang.blog.presentation.request.BlogUnsubscribeRequest;
import com.mallang.blog.query.BlogSubscribeQueryService;
import com.mallang.blog.query.response.SubscriberResponse;
import com.mallang.blog.query.response.SubscribingBlogResponse;
import com.mallang.common.presentation.PageResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/blog-subscribes")
@RestController
public class BlogSubscribeController {

    private final BlogSubscribeService blogSubscribeService;
    private final BlogSubscribeQueryService blogSubscribeQueryService;

    @PostMapping
    public ResponseEntity<Void> subscribe(
            @Auth Long memberId,
            @RequestBody BlogSubscribeRequest request
    ) {
        Long id = blogSubscribeService.subscribe(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/blog-subscribes/" + id)).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unsubscribe(
            @Auth Long memberId,
            @RequestBody BlogUnsubscribeRequest request
    ) {
        blogSubscribeService.unsubscribe(request.toCommand(memberId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/subscribers")
    public ResponseEntity<PageResponse<SubscriberResponse>> findSubscribers(
            @RequestParam(name = "blogName", required = true) String blogName,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponse.from(blogSubscribeQueryService.findSubscribers(blogName, pageable)));
    }

    @GetMapping("/subscribing-blogs")
    public ResponseEntity<PageResponse<SubscribingBlogResponse>> findSubscribingBlogs(
            @RequestParam(name = "memberId", required = true) Long memberId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponse.from(blogSubscribeQueryService.findSubscribingBlogs(memberId, pageable)));
    }
}
