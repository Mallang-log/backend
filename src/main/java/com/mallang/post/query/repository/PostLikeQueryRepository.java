package com.mallang.post.query.repository;

import com.mallang.auth.domain.Member;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.like.PostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeQueryRepository extends JpaRepository<PostLike, Long> {

    default boolean existsByMemberAndPost(
            Member member,
            Post post
    ) {
        return findByMemberAndPost(member, post).isPresent();
    }

    Optional<PostLike> findByMemberAndPost(Member member, Post post);
}
