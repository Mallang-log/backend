package com.mallang.blog.domain;

import com.mallang.blog.exception.AlreadyExistAboutException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AboutValidator {

    private final AboutRepository aboutRepository;

    public void validateAlreadyExist(Blog blog) {
        if (aboutRepository.existsByBlog(blog)) {
            throw new AlreadyExistAboutException();
        }
    }
}
