package com.mallang.blog.query;

import com.mallang.blog.query.dao.BlogDao;
import com.mallang.blog.query.response.BlogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlogQueryService {

    private final BlogDao blogDao;

    public BlogResponse findByName(String blogName) {
        return blogDao.find(blogName);
    }
}
