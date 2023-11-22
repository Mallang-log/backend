package com.mallang.blog.query.support;

import com.mallang.blog.domain.About;
import com.mallang.blog.exception.NotFoundAboutException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AboutQuerySupport extends JpaRepository<About, Long> {

    default About getByBlogId(Long blogId) {
        return findByBlogId(blogId)
                .orElseThrow(NotFoundAboutException::new);
    }

    Optional<About> findByBlogId(Long blogId);
}
