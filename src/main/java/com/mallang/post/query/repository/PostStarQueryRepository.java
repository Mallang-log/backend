package com.mallang.post.query.repository;

import com.mallang.post.domain.star.PostStar;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostStarQueryRepository extends
        JpaRepository<PostStar, Long>,
        StaredPostDao {

    @EntityGraph(attributePaths = {"post", "member"})
    List<PostStar> findAllWithPostAndMemberByMemberIdOrderByIdDesc(Long memberId);
}
