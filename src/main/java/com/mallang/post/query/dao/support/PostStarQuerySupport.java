package com.mallang.post.query.dao.support;

import com.mallang.post.domain.star.PostStar;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostStarQuerySupport extends JpaRepository<PostStar, Long> {

    @EntityGraph(attributePaths = {"post", "member"})
    List<PostStar> findAllWithPostAndMemberByMemberIdOrderByIdDesc(Long memberId);
}
