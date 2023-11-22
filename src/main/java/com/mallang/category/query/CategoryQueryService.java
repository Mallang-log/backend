package com.mallang.category.query;

import com.mallang.category.query.dao.CategoryDao;
import com.mallang.category.query.response.CategoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryQueryService {

    private final CategoryDao categoryDao;

    public List<CategoryResponse> findAllByBlogName(String blogName) {
        return categoryDao.findAllByBlogName(blogName);
    }
}
