package com.mallang.blog.query;

import com.mallang.blog.query.repository.AboutQueryRepository;
import com.mallang.blog.query.response.AboutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AboutQueryService {

    private final AboutQueryRepository aboutQueryRepository;

    public AboutResponse findByBlogName(String blogName) {
        return AboutResponse.from(aboutQueryRepository.getByBlogName(blogName));
    }
}
