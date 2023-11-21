package com.mallang.blog.query.dao;

import com.mallang.blog.query.dao.support.AboutQuerySupport;
import com.mallang.blog.query.data.AboutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class AboutResponseDao {

    private final AboutQuerySupport aboutQuerySupport;

    public AboutResponse find(String blogName) {
        return AboutResponse.from(aboutQuerySupport.getByBlogName(blogName));
    }
}
