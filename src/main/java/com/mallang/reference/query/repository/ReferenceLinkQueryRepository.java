package com.mallang.reference.query.repository;

import com.mallang.auth.domain.Member;
import com.mallang.reference.domain.ReferenceLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReferenceLinkQueryRepository extends
        JpaRepository<ReferenceLink, Long>,
        ReferenceLinkSearchDao {

    @Query("SELECT COUNT(rl) > 0 FROM ReferenceLink rl WHERE rl.member = :member AND rl.url.url = :url")
    boolean existsByMemberAndUrl(@Param("member") Member member, @Param("url") String url);
}
