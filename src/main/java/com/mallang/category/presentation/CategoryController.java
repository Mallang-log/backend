package com.mallang.category.presentation;

import com.mallang.blog.domain.BlogName;
import com.mallang.category.application.CategoryService;
import com.mallang.category.presentation.request.CreateCategoryRequest;
import com.mallang.category.presentation.request.DeleteCategoryRequest;
import com.mallang.category.presentation.request.UpdateCategoryRequest;
import com.mallang.category.query.CategoryQueryService;
import com.mallang.category.query.data.CategoryData;
import com.mallang.common.auth.Auth;
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
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryQueryService categoryQueryService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Auth Long memberId,
            @RequestBody CreateCategoryRequest request
    ) {
        Long categoryId = categoryService.create(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/categories/" + categoryId)).build();
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> update(
            @PathVariable(name = "categoryId") Long categoryId,
            @Auth Long memberId,
            @RequestBody UpdateCategoryRequest request
    ) {
        categoryService.update(request.toCommand(categoryId, memberId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(
            @PathVariable(name = "categoryId") Long categoryId,
            @Auth Long memberId,
            @RequestBody DeleteCategoryRequest request
    ) {
        categoryService.delete(request.toCommand(categoryId, memberId));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoryData>> findAllByBlogAndMember(
            @RequestParam(name = "blogName", required = true) BlogName blogName,
            @Auth Long memberId
    ) {
        List<CategoryData> result = categoryQueryService.findAllByMemberIdAndBlogName(memberId, blogName);
        return ResponseEntity.ok(result);
    }
}
