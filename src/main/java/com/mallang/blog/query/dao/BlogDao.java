package com.mallang.blog.query.dao;

import com.mallang.blog.query.response.BlogResponse;
import com.mallang.blog.query.support.BlogQuerySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class BlogDao {

    private final BlogQuerySupport blogQuerySupport;

    public BlogResponse find(String blogName) {
        return BlogResponse.from(blogQuerySupport.getWithOwnerByName(blogName));
    }
}
