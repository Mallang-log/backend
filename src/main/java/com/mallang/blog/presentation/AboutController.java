package com.mallang.blog.presentation;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.blog.application.AboutService;
import com.mallang.blog.presentation.request.DeleteAboutRequest;
import com.mallang.blog.presentation.request.UpdateAboutRequest;
import com.mallang.blog.presentation.request.WriteAboutRequest;
import com.mallang.blog.query.AboutQueryService;
import com.mallang.blog.query.data.AboutResponse;
import java.net.URI;
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
@RequestMapping("/abouts")
@RestController
public class AboutController {

    private final AboutService aboutService;
    private final AboutQueryService aboutQueryService;

    @PostMapping
    public ResponseEntity<Long> write(
            @Auth Long memberId,
            @RequestBody WriteAboutRequest request
    ) {
        Long aboutId = aboutService.write(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/abouts/" + aboutId)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("id") Long aboutId,
            @Auth Long memberId,
            @RequestBody UpdateAboutRequest request
    ) {
        aboutService.update(request.toCommand(aboutId, memberId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long aboutId,
            @Auth Long memberId,
            @RequestBody DeleteAboutRequest request
    ) {
        aboutService.delete(request.toCommand(aboutId, memberId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<AboutResponse> findByBlogName(
            @RequestParam(name = "blogName") String blogName
    ) {
        return ResponseEntity.ok(aboutQueryService.findByBlogName(blogName));
    }
}
