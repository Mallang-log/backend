package com.mallang.blog.domain;

import com.mallang.blog.exception.NotFoundBlogException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    default Blog getByName(String blogName) {
        return findByName(blogName).orElseThrow(NotFoundBlogException::new);
    }

    @Query("SELECT b FROM Blog b WHERE b.name.value = :blogName")
    Optional<Blog> findByName(@Param("blogName") String blogName);

    boolean existsByName(BlogName name);

    boolean existsByOwnerId(Long ownerId);
}
