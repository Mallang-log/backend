package com.mallang.blog.domain;

import com.mallang.blog.exception.NotFoundAboutException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AboutRepository extends JpaRepository<About, Long> {

    boolean existsByBlog(Blog blog);

    default About getByIdAndWriterIdAndBlogId(Long aboutId, Long memberId, Long blogId) {
        return findByIdAndWriterIdAndBlogId(aboutId, memberId, blogId)
                .orElseThrow(NotFoundAboutException::new);
    }

    Optional<About> findByIdAndWriterIdAndBlogId(Long aboutId, Long writerId, Long blogId);
}
