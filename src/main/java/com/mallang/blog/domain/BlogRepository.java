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

    default Blog getByNameAndOwner(String blogName, Long ownerId) {
        return findByNameAndOwner(blogName, ownerId)
                .orElseThrow(() ->
                        new NotFoundBlogException("존재하지 않는 블로그거나, 해당 사용자의 블로그가 아닙니다."));
    }

    @Query("SELECT b FROM Blog b WHERE b.name.value = :blogName AND b.owner.id = :ownerId")
    Optional<Blog> findByNameAndOwner(
            @Param("blogName") String blogName,
            @Param("ownerId") Long ownerId
    );

    boolean existsByName(BlogName name);

    boolean existsByOwnerId(Long ownerId);
}
