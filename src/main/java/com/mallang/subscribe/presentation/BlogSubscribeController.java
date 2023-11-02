package com.mallang.subscribe.presentation;

import com.mallang.common.auth.Auth;
import com.mallang.subscribe.application.BlogSubscribeService;
import com.mallang.subscribe.presentation.request.BlogSubscribeRequest;
import com.mallang.subscribe.presentation.request.BlogUnsubscribeRequest;
import com.mallang.subscribe.query.BlogSubscribeQueryService;
import com.mallang.subscribe.query.data.SubscriberData;
import com.mallang.subscribe.query.data.SubscribingBlogData;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    public List<SubscriberData> findSubscribers(
            @RequestParam(name = "blogId", required = true) Long blogId
    ) {
        return blogSubscribeQueryService.findSubscribers(blogId);
    }

    @GetMapping("/subscribing-blogs")
    public List<SubscribingBlogData> findSubscribingBlogs(
            @RequestParam(name = "blogId", required = true) Long memberId
    ) {
        return blogSubscribeQueryService.findSubscribingBlogs(memberId);
    }
}
