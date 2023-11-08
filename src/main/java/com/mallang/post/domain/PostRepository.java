package com.mallang.post.domain;

import com.mallang.blog.domain.Blog;
import com.mallang.post.exception.NotFoundPostException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    default Post getById(Long id) {
        return findById(id).orElseThrow(NotFoundPostException::new);
    }

    List<Post> findAllByCategoryId(Long categoryId);

    List<Post> findAllByIdIn(List<Long> ids);

    Long countByBlog(Blog blog);
}
