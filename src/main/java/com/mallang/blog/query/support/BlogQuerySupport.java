package com.mallang.blog.query.support;

import com.mallang.blog.domain.Blog;
import com.mallang.blog.exception.NotFoundBlogException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlogQuerySupport extends JpaRepository<Blog, Long> {

    default Blog getWithOwnerByName(String blogName) {
        return findWithOwnerByName(blogName).orElseThrow(NotFoundBlogException::new);
    }

    @Query("SELECT b FROM Blog b JOIN FETCH b.owner WHERE b.name.value = :blogName")
    Optional<Blog> findWithOwnerByName(String blogName);
}
