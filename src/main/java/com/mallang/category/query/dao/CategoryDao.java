package com.mallang.category.query.dao;

import com.mallang.category.query.response.CategoryResponse;
import com.mallang.category.query.support.CategoryQuerySupport;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class CategoryDao {

    private final CategoryQuerySupport categoryQuerySupport;

    public List<CategoryResponse> findAllByBlogId(Long blogId) {
        return categoryQuerySupport.findAllRootByBlogId(blogId)
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
