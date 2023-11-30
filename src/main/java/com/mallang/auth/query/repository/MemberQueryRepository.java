package com.mallang.auth.query.repository;

import com.mallang.auth.domain.Member;
import com.mallang.auth.exception.NotFoundMemberException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberQueryRepository extends JpaRepository<Member, Long> {

    default Member getById(Long id) {
        return findById(id)
                .orElseThrow(NotFoundMemberException::new);
    }
}
