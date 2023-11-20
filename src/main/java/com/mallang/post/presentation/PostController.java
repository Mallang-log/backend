package com.mallang.post.presentation;

import static com.mallang.post.presentation.support.PostPresentationConstant.POST_PASSWORD_COOKIE;

import com.mallang.auth.presentation.support.OptionalAuth;
import com.mallang.post.query.PostQueryService;
import com.mallang.post.query.data.PostDetailData;
import com.mallang.post.query.data.PostSearchCond;
import com.mallang.post.query.data.PostSimpleData;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostQueryService postQueryService;

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailData> getById(
            @OptionalAuth Long memberId,
            @CookieValue(name = POST_PASSWORD_COOKIE, required = false) String postPassword,
            @PathVariable(name = "id") Long id
    ) {
        return ResponseEntity.ok(postQueryService.getById(memberId, postPassword, id));
    }

    @GetMapping
    public ResponseEntity<List<PostSimpleData>> search(
            @OptionalAuth Long memberId,
            @ModelAttribute PostSearchCond postSearchCond
    ) {
        return ResponseEntity.ok(postQueryService.search(memberId, postSearchCond));
    }
}
