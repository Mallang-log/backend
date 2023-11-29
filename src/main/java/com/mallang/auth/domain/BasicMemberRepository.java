package com.mallang.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicMemberRepository extends JpaRepository<BasicMember, Long> {

    boolean existsByUsername(String username);
}
