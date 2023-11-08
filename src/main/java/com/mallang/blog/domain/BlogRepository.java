package com.mallang.blog.domain;

import com.mallang.blog.exception.NotFoundBlogException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Override
    default Blog getById(Long id) {
        return findById(id).orElseThrow(NotFoundBlogException::new);
    }

    boolean existsByName(BlogName name);

    boolean existsByOwnerId(Long ownerId);
}
