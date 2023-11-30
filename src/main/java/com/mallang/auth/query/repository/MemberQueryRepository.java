package com.mallang.auth.query.repository;

import com.mallang.auth.domain.Member;
import com.mallang.auth.exception.NotFoundMemberException;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberQueryRepository extends JpaRepository<Member, Long> {

    @Nullable
    default Member getMemberIfIdNotNull(@Nullable Long id) {
        if (id == null) {
            return null;
        }
        return getById(id);
    }

    default Member getById(Long id) {
        return findById(id)
                .orElseThrow(NotFoundMemberException::new);
    }
}
