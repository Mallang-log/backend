package com.mallang.post.domain;

import com.mallang.post.exception.NotFoundPostException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, PostId> {

    default Post getById(Long postId, String blogName) {
        return findById(postId, blogName)
                .orElseThrow(NotFoundPostException::new);
    }

    @Query("SELECT p FROM Post p WHERE p.postId.id = :postId AND p.blog.name.value = :blogName")
    Optional<Post> findById(
            @Param("postId") Long postId,
            @Param("blogName") String blogName
    );

    @Query("SELECT p FROM Post p WHERE p.content.category.id = :categoryId")
    List<Post> findAllByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Post p WHERE p.postId.id in :ids AND p.blog.name.value = :blogName")
    List<Post> findAllByIdIn(
            @Param("ids") List<Long> ids,
            @Param("blogName") String blogName
    );

    @Query("SELECT COUNT(p) FROM Post p WHERE p.postId.blogId = :blogId")
    Long countByBlog(Long blogId);
}
