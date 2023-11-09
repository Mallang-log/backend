package com.mallang.post.presentation;

import static com.mallang.post.presentation.support.PostPresentationConstant.PROTECTED_PASSWORD_HEADER;
import static com.mallang.post.presentation.support.PostPresentationConstant.PROTECTED_PASSWORD_SESSION;

import com.mallang.common.auth.Auth;
import com.mallang.common.auth.OptionalAuth;
import com.mallang.post.application.PostService;
import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.post.presentation.request.DeletePostRequest;
import com.mallang.post.presentation.request.UpdatePostRequest;
import com.mallang.post.query.PostQueryService;
import com.mallang.post.query.data.PostDetailData;
import com.mallang.post.query.data.PostSearchCond;
import com.mallang.post.query.data.PostSimpleData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailData> getById(
            @RequestHeader(name = PROTECTED_PASSWORD_HEADER, required = false) String password,
            @OptionalAuth Long memberId,
            @PathVariable(name = "id") Long id,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(postQueryService.getById(memberId, id));
    }

    @GetMapping(path = "/{id}", headers = {PROTECTED_PASSWORD_HEADER})
    public ResponseEntity<PostDetailData> getProtectedById(
            @RequestHeader(name = PROTECTED_PASSWORD_HEADER) String password,
            @OptionalAuth Long memberId,
            @PathVariable(name = "id") Long id,
            HttpServletRequest request
    ) {
        PostDetailData data = postQueryService.getProtectedById(memberId, id, password);
        HttpSession session = request.getSession(true);
        session.setAttribute(PROTECTED_PASSWORD_SESSION, password);
        session.setMaxInactiveInterval(86400);
        return ResponseEntity.ok(data);
    }

    @GetMapping
    public ResponseEntity<List<PostSimpleData>> search(
            @OptionalAuth Long memberId,
            @ModelAttribute PostSearchCond postSearchCond
    ) {
        return ResponseEntity.ok(postQueryService.search(memberId, postSearchCond));
    }
}
