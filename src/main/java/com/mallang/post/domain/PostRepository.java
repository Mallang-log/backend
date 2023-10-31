package com.mallang.post.domain;

import com.mallang.blog.domain.BlogName;
import com.mallang.post.exception.NotFoundPostException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    default Post getById(Long id) {
        return findById(id).orElseThrow(NotFoundPostException::new);
    }

    @Query("SELECT p FROM Post p WHERE p.blog.name = :blogName AND p.category.id = :categoryId")
    List<Post> findAllByBlogNameAndCategoryId(
            @Param("blogName") BlogName blogName,
            @Param("categoryId") Long categoryId
    );
}
