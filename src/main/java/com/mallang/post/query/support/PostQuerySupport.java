package com.mallang.post.query.support;

import com.mallang.post.domain.Post;
import com.mallang.post.exception.NotFoundPostException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostQuerySupport extends JpaRepository<Post, Long> {

    default Post getById(Long id) {
        return findById(id).orElseThrow(NotFoundPostException::new);
    }

    default Post getByIdAndWriterId(Long id, Long writerId) {
        return findByIdAndWriterId(id, writerId).orElseThrow(NotFoundPostException::new);
    }

    Optional<Post> findByIdAndWriterId(Long id, Long writerId);
}
