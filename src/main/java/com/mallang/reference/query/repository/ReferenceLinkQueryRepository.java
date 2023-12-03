package com.mallang.reference.query.repository;

import com.mallang.reference.domain.ReferenceLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferenceLinkQueryRepository extends
        JpaRepository<ReferenceLink, Long>,
        ReferenceLinkSearchDao {
}
