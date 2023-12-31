package com.mallang.blog.domain;

import com.mallang.blog.exception.DuplicateBlogNameException;
import com.mallang.blog.exception.TooManyBlogsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BlogValidator {

    private final BlogRepository blogRepository;

    public void validateOpen(Long memberId, BlogName name) {
        if (blogRepository.existsByOwnerId(memberId)) {
            throw new TooManyBlogsException();
        }
        if (blogRepository.existsByName(name)) {
            throw new DuplicateBlogNameException();
        }
    }
}
