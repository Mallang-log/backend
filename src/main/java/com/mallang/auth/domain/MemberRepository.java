package com.mallang.auth.domain;

import com.mallang.auth.exception.NotFoundMemberException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    default Member getById(Long id) {
        return findById(id).orElseThrow(NotFoundMemberException::new);
    }
}
