package com.mallang.blog.domain;

import com.mallang.blog.exception.NotFoundAboutException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AboutRepository extends JpaRepository<About, Long> {

    boolean existsByBlog(Blog blog);

    default About getByIdAndWriterIdAndBlogName(Long aboutId, Long memberId, String blogName) {
        return findByWriterIdAndBlogName(aboutId, memberId, blogName)
                .orElseThrow(NotFoundAboutException::new);
    }

    @Query("SELECT a FROM About a WHERE a.id = :aboutId AND a.writer.id = :writerId AND a.blog.name.value = :blogName")
    Optional<About> findByWriterIdAndBlogName(
            @Param("aboutId") Long aboutId,
            @Param("writerId") Long writerId,
            @Param("blogName") String blogName
    );
}
