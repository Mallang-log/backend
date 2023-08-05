package com.mallang.category.presentation;

import com.mallang.category.application.CategoryService;
import com.mallang.category.presentation.request.CreateCategoryRequest;
import com.mallang.category.presentation.request.UpdateCategoryRequest;
import com.mallang.common.auth.Auth;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

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
            @PathVariable Long categoryId,
            @Auth Long memberId,
            @RequestBody UpdateCategoryRequest request
    ) {
        categoryService.update(request.toCommand(categoryId, memberId));
        return ResponseEntity.ok().build();
    }
}
