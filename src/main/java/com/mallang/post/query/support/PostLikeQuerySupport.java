package com.mallang.post.query.support;

import com.mallang.post.domain.like.PostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeQuerySupport extends JpaRepository<PostLike, Long> {

    default boolean existsByMemberIdAndPostId(
            Long memberId,
            Long postId,
            String blogName
    ) {
        return findByMemberIdAndPostId(memberId, postId, blogName).isPresent();
    }

    @Query("""
            SELECT pl FROM PostLike pl
            WHERE pl.member.id = :memberId
            AND pl.post.postId.id = :postId
            AND pl.post.blog.name.value = :blogName
            """)
    Optional<PostLike> findByMemberIdAndPostId(
            @Param("memberId") Long memberId,
            @Param("postId") Long postId,
            @Param("blogName") String blogName
    );
}
