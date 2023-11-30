package com.mallang.post.domain.draft;

import com.mallang.post.exception.NotFoundDraftException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DraftRepository extends JpaRepository<Draft, Long> {

    default Draft getById(Long id) {
        return findById(id)
                .orElseThrow(NotFoundDraftException::new);
    }
}
