package com.mallang.category.application;

import com.mallang.category.application.query.CategoryResponse;
import com.mallang.category.domain.Category;
import com.mallang.category.domain.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryQueryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> findAllByMemberId(Long memberId) {
        List<Category> categories = categoryRepository.findAllRootByMemberId(memberId);
        return categories.stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
