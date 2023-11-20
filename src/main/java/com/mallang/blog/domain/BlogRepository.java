package com.mallang.blog.domain;

import com.mallang.blog.exception.NotFoundBlogException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Override
    default Blog getById(Long id) {
        return findById(id).orElseThrow(NotFoundBlogException::new);
    }

    default Blog getByIdAndOwnerId(Long id, Long ownerId) {
        return findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() ->
                        new NotFoundBlogException("존재하지 않는 블로그거나, 해당 사용자의 블로그가 아닙니다."));
    }

    Optional<Blog> findByIdAndOwnerId(Long id, Long ownerId);

    boolean existsByName(BlogName name);

    boolean existsByOwnerId(Long ownerId);
}
