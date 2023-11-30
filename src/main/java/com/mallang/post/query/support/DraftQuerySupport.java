package com.mallang.post.query.support;

import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.draft.Draft;
import com.mallang.post.exception.NotFoundDraftException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DraftQuerySupport extends JpaRepository<Draft, Long> {

    default Draft getById(Long id) {
        return findById(id).orElseThrow(NotFoundDraftException::new);
    }

    List<Draft> findAllByBlogOrderByUpdatedDateDesc(Blog blog);
}
