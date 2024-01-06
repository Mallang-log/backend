package com.mallang.auth.query.repository;

import com.mallang.auth.domain.BasicMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicMemberQueryRepository extends JpaRepository<BasicMember, Long> {

    boolean existsByUsername(String username);
}
