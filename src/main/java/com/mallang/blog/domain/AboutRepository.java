package com.mallang.blog.domain;

import com.mallang.blog.exception.NotFoundAboutException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AboutRepository extends JpaRepository<About, Long> {

    default About getById(Long id) {
        return findById(id).orElseThrow(NotFoundAboutException::new);
    }

    boolean existsByBlog(Blog blog);
}
