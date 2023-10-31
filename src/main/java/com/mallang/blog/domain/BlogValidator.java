package com.mallang.blog.domain;

import com.mallang.blog.exception.DuplicateBlogNameException;
import com.mallang.blog.exception.TooManyBlogsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BlogValidator {

    private final BlogRepository blogRepository;

    public void validateOpen(Long memberId, String name) {
        if (blogRepository.existsByMemberId(memberId)) {
            throw new TooManyBlogsException();
        }
        if (blogRepository.existsByName(name)) {
            throw new DuplicateBlogNameException();
        }
    }
}
