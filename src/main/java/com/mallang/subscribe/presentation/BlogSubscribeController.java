package com.mallang.subscribe.presentation;

import com.mallang.common.auth.Auth;
import com.mallang.subscribe.application.BlogSubscribeService;
import com.mallang.subscribe.presentation.request.BlogSubscribeRequest;
import com.mallang.subscribe.presentation.request.BlogUnsubscribeRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/blog-subscribes")
@RestController
public class BlogSubscribeController {

    private final BlogSubscribeService blogSubscribeService;

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
}
