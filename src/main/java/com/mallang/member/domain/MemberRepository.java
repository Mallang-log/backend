package com.mallang.member.domain;

import com.mallang.member.exception.NotFoundMemberException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    default Member getById(Long id) {
        return findById(id).orElseThrow(NotFoundMemberException::new);
    }

    Optional<Member> findByOauthId(OauthId oauthId);
}
