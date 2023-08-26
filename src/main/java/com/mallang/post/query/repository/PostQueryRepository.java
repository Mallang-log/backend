package com.mallang.post.query.repository;

import com.mallang.post.domain.Post;
import com.mallang.post.exception.NotFoundPostException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostQueryRepository extends JpaRepository<Post, Long>, PostQueryDslRepository {

    default Post getById(Long id) {
        return findById(id).orElseThrow(NotFoundPostException::new);
    }
}
