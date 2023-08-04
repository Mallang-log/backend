package com.mallang.post.domain;

import com.mallang.post.exception.NotFoundPostException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    default Post getById(Long id) {
        return findById(id).orElseThrow(NotFoundPostException::new);
    }
}
