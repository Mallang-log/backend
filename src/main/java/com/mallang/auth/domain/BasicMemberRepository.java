package com.mallang.auth.domain;

import com.mallang.auth.exception.NotFoundMemberException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicMemberRepository extends JpaRepository<BasicMember, Long> {

    boolean existsByUsername(String username);

    default BasicMember getByUsername(String username) {
        return findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);
    }

    Optional<BasicMember> findByUsername(String username);
}
