package com.mallang.auth.domain;

import com.mallang.auth.exception.NotFoundMemberException;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    default Member getByIdIfIdNotNull(@Nullable Long id) {
        if (id == null) {
            return null;
        }
        return getById(id);
    }

    default Member getById(Long id) {
        return findById(id).orElseThrow(NotFoundMemberException::new);
    }
}
