package com.mallang.post.presentation;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.post.application.DraftService;
import com.mallang.post.application.command.DeleteDraftCommand;
import com.mallang.post.presentation.request.CreateDraftRequest;
import com.mallang.post.presentation.request.UpdateDraftRequest;
import com.mallang.post.query.DraftQueryService;
import com.mallang.post.query.response.DraftDetailResponse;
import com.mallang.post.query.response.DraftListResponse;
import java.net.URI;
import java.util.List;
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
@RequestMapping("/drafts")
@RestController
public class DraftController {

    private final DraftService draftService;
    private final DraftQueryService draftQueryService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Auth Long memberId,
            @RequestBody CreateDraftRequest request
    ) {
        Long id = draftService.create(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/drafts/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable(name = "id") Long draftId,
            @Auth Long memberId,
            @RequestBody UpdateDraftRequest request
    ) {
        draftService.update(request.toCommand(memberId, draftId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable(name = "id") Long draftId,
            @Auth Long memberId
    ) {
        draftService.delete(new DeleteDraftCommand(memberId, draftId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DraftListResponse>> findAllByBlog(
            @Auth Long memberId,
            @RequestParam("blogName") String blogName
    ) {
        return ResponseEntity.ok(draftQueryService.findAllByBlog(memberId, blogName));
    }

    @GetMapping("{id}")
    public ResponseEntity<DraftDetailResponse> fingById(
            @PathVariable(name = "id") Long draftId,
            @Auth Long memberId
    ) {
        return ResponseEntity.ok(draftQueryService.findById(memberId, draftId));
    }
}
