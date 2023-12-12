package com.mallang.reference.domain;

import com.mallang.auth.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {

    boolean existsByOwner(Member member);

    boolean existsByOwnerAndName(Member member, String name);
}
