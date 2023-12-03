package com.mallang.post.presentation;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.common.presentation.PageResponse;
import com.mallang.post.application.PostService;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.domain.PostId;
import com.mallang.post.presentation.request.CreatePostFromDraftRequest;
import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.post.presentation.request.DeletePostRequest;
import com.mallang.post.presentation.request.UpdatePostRequest;
import com.mallang.post.query.PostManageQueryService;
import com.mallang.post.query.repository.PostManageSearchDao.PostManageSearchCond;
import com.mallang.post.query.response.PostManageDetailResponse;
import com.mallang.post.query.response.PostManageSearchResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private final PostManageQueryService postManageQueryService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Auth Long memberId,
            @RequestBody CreatePostRequest request
    ) {
        PostId id = postService.create(request.toCommand(memberId));
        URI uri = URI.create("/posts/%s/%d".formatted(request.blogName(), id.getPostId()));
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/from-draft")
    public ResponseEntity<Void> create(
            @Auth Long memberId,
            @RequestBody CreatePostFromDraftRequest request
    ) {
        CreatePostRequest createPostRequest = request.createPostRequest();
        CreatePostCommand command = createPostRequest.toCommand(memberId);
        PostId id = postService.createFromDraft(command, request.draftId());
        URI uri = URI.create("/posts/%s/%d".formatted(createPostRequest.blogName(), id.getPostId()));
        return ResponseEntity.created(uri).build();
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

    @GetMapping("/{blogName}/{id}")
    public ResponseEntity<PostManageDetailResponse> getById(
            @PathVariable(name = "blogName") String blogName,
            @PathVariable(name = "id") Long postId,
            @Auth Long memberId
    ) {
        return ResponseEntity.ok(postManageQueryService.getById(memberId, postId, blogName));
    }

    @GetMapping("/{blogName}")
    public ResponseEntity<PageResponse<PostManageSearchResponse>> search(
            @Auth Long memberId,
            @PathVariable(name = "blogName") String blogName,
            @ModelAttribute PostManageSearchCond cond,
            @PageableDefault(size = 15) Pageable pageable
    ) {
        Page<PostManageSearchResponse> response = postManageQueryService.search(memberId, blogName, cond, pageable);
        return ResponseEntity.ok(PageResponse.from(response));
    }
}
