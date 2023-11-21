package com.mallang.blog.query;

import com.mallang.blog.query.dao.AboutResponseDao;
import com.mallang.blog.query.data.AboutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AboutQueryService {

    private final AboutResponseDao aboutResponseDao;

    public AboutResponse findByBlogName(String blogName) {
        return aboutResponseDao.find(blogName);
    }
}
