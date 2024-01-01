package com.mallang.blog.query.repository;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.exception.NotFoundBlogException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlogQueryRepository extends JpaRepository<Blog, Long> {

    default Blog getWithOwnerByName(String blogName) {
        return findWithOwnerByName(blogName).orElseThrow(NotFoundBlogException::new);
    }

    default Blog getByName(String blogName) {
        return findByName(blogName).orElseThrow(NotFoundBlogException::new);
    }

    @Query("SELECT b FROM Blog b WHERE b.name.value = :blogName")
    Optional<Blog> findByName(@Param("blogName") String blogName);

    @Query("SELECT b FROM Blog b JOIN FETCH b.owner WHERE b.name.value = :blogName")
    Optional<Blog> findWithOwnerByName(String blogName);

    default Blog getByMemberAndBlog(Long memberId, String blogName) {
        return findByMemberAndBlog(memberId, blogName)
                .orElseThrow(NotFoundBlogException::new);
    }

    @Query("SELECT b FROM Blog b WHERE b.name.value = :blogName AND b.owner.id = :memberId")
    Optional<Blog> findByMemberAndBlog(Long memberId, String blogName);

    default Blog getByOwner(Member owner) {
        return findByOwner(owner)
                .orElseThrow(NotFoundBlogException::new);
    }

    Optional<Blog> findByOwner(Member owner);
}
