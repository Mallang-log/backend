package com.mallang.post.domain;

import com.mallang.blog.domain.Blog;
import com.mallang.post.exception.NotFoundPostException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    default Post getById(Long id) {
        return findById(id).orElseThrow(NotFoundPostException::new);
    }

    default Post getByIdAndWriterId(Long id, Long writerId) {
        return findByIdAndWriterId(id, writerId).orElseThrow(NotFoundPostException::new);
    }

    Optional<Post> findByIdAndWriterId(Long id, Long writerId);

    List<Post> findAllByCategoryId(Long categoryId);

    List<Post> findAllByIdInAndWriterId(List<Long> ids, Long writerId);

    Long countByBlog(Blog blog);
}
