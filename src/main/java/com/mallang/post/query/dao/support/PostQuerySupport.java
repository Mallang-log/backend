package com.mallang.post.query.dao.support;

import com.mallang.post.domain.Post;
import com.mallang.post.exception.NotFoundPostException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostQuerySupport extends JpaRepository<Post, Long> {

    default Post getById(Long id) {
        return findById(id).orElseThrow(NotFoundPostException::new);
    }
}
