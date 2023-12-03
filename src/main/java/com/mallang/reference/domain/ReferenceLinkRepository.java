package com.mallang.reference.domain;

import com.mallang.reference.exception.NotFoundReferenceLinkException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferenceLinkRepository extends JpaRepository<ReferenceLink, Long> {

    default ReferenceLink getById(Long id) {
        return findById(id)
                .orElseThrow(NotFoundReferenceLinkException::new);
    }
}
