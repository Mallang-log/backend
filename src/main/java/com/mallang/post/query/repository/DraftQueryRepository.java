package com.mallang.post.query.repository;

import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.draft.Draft;
import com.mallang.post.exception.NotFoundDraftException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DraftQueryRepository extends JpaRepository<Draft, Long> {

    default Draft getById(Long id) {
        return findById(id).orElseThrow(NotFoundDraftException::new);
    }

    List<Draft> findAllByBlogOrderByUpdatedDateDesc(Blog blog);
}
