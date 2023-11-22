package com.mallang.blog.query.dao;

import com.mallang.blog.query.response.AboutResponse;
import com.mallang.blog.query.support.AboutQuerySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class AboutDao {

    private final AboutQuerySupport aboutQuerySupport;

    public AboutResponse find(Long blogId) {
        return AboutResponse.from(aboutQuerySupport.getByBlogId(blogId));
    }
}
