package com.mallang.blog.query;

import com.mallang.blog.query.repository.BlogQueryRepository;
import com.mallang.blog.query.response.BlogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlogQueryService {

    private final BlogQueryRepository blogQueryRepository;

    public BlogResponse findByName(String blogName) {
        return BlogResponse.from(blogQueryRepository.getWithOwnerByName(blogName));
    }
}
