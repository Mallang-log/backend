package com.mallang.comment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticatedWriterRepository extends JpaRepository<AuthenticatedWriter, Long> {

    AuthenticatedWriter getByMemberId(Long memberId);
}
