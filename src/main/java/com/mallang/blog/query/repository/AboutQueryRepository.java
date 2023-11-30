package com.mallang.blog.query.repository;

import com.mallang.blog.domain.About;
import com.mallang.blog.exception.NotFoundAboutException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AboutQueryRepository extends JpaRepository<About, Long> {

    default About getByBlogName(String blogName) {
        return findByBlogName(blogName)
                .orElseThrow(NotFoundAboutException::new);
    }

    @Query("SELECT a FROM About a WHERE a.blog.name.value = :blogName")
    Optional<About> findByBlogName(@Param("blogName") String blogName);
}
