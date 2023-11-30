package com.mallang.category.query;

import com.mallang.category.query.repository.CategoryQueryRepository;
import com.mallang.category.query.response.CategoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryQueryService {

    private final CategoryQueryRepository categoryQueryRepository;

    public List<CategoryResponse> findAllByBlogName(String blogName) {
        return categoryQueryRepository.findAllRootByBlogName(blogName)
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
