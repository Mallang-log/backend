package com.mallang.category.presentation;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.category.application.PostCategoryService;
import com.mallang.category.application.command.DeletePostCategoryCommand;
import com.mallang.category.presentation.request.CreatePostCategoryRequest;
import com.mallang.category.presentation.request.UpdatePostCategoryHierarchyRequest;
import com.mallang.category.presentation.request.UpdatePostCategoryNameRequest;
import com.mallang.category.query.PostCategoryQueryService;
import com.mallang.category.query.response.PostCategoryResponse;
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
@RequestMapping("/categories")
@RestController
public class PostCategoryController {

    private final PostCategoryService postCategoryService;
    private final PostCategoryQueryService postCategoryQueryService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Auth Long memberId,
            @RequestBody CreatePostCategoryRequest request
    ) {
        Long categoryId = postCategoryService.create(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/categories/" + categoryId)).build();
    }

    @PutMapping("/{categoryId}/hierarchy")
    public ResponseEntity<Void> updateHierarchy(
            @PathVariable(name = "categoryId") Long categoryId,
            @Auth Long memberId,
            @RequestBody UpdatePostCategoryHierarchyRequest request
    ) {
        postCategoryService.updateHierarchy(request.toCommand(categoryId, memberId));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{categoryId}/name")
    public ResponseEntity<Void> updateName(
            @PathVariable(name = "categoryId") Long categoryId,
            @Auth Long memberId,
            @RequestBody UpdatePostCategoryNameRequest request
    ) {
        postCategoryService.updateName(request.toCommand(categoryId, memberId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(
            @PathVariable(name = "categoryId") Long categoryId,
            @Auth Long memberId
    ) {
        postCategoryService.delete(new DeletePostCategoryCommand(memberId, categoryId));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PostCategoryResponse>> findAllByBlog(
            @RequestParam(name = "blogName", required = true) String blogName
    ) {
        List<PostCategoryResponse> result = postCategoryQueryService.findAllByBlogName(blogName);
        return ResponseEntity.ok(result);
    }
}
