package com.mallang.post.domain;

import com.mallang.post.exception.NotFoundPostException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, PostId> {

    default Post getById(Long postId, String blogName) {
        return findById(postId, blogName).orElseThrow(NotFoundPostException::new);
    }

    @Query("SELECT p FROM Post p WHERE p.postId.id = :postId AND p.blog.name.value = :blogName")
    Optional<Post> findById(@Param("postId") Long postId,
                            @Param("blogName") String blogName);

    default Post getByIdAndWriter(Long postId, String blogName, Long writerId) {
        return findByIdAndWriter(postId, blogName, writerId)
                .orElseThrow(NotFoundPostException::new);
    }

    @Query("SELECT p FROM Post p WHERE p.postId.id = :postId AND p.blog.name.value = :blogName AND p.writer.id = :writerId")
    Optional<Post> findByIdAndWriter(@Param("postId") Long postId,
                                     @Param("blogName") String blogName,
                                     @Param("writerId") Long writerId);

    List<Post> findAllByCategoryId(Long categoryId);

    @Query("SELECT p FROM Post p WHERE p.postId.id in :ids AND p.blog.name.value = :blogName AND p.writer.id = :writerId")
    List<Post> findAllByIdInAndWriter(@Param("ids") List<Long> ids,
                                      @Param("blogName") String blogName,
                                      @Param("writerId") Long writerId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.postId.blogId = :blogId")
    Long countByBlog(Long blogId);
}
