package com.mallang.post.query.dao.support;

import com.mallang.post.domain.like.PostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeQuerySupport extends JpaRepository<PostLike, Long> {

    default boolean existsByMemberIdAndPostId(
            Long memberId,
            Long postId
    ) {
        // TODO 이거 VS findTop1ByMemberIdAndBlogNameAndPostId 이거랑 성능차이 있나 확인하기
        return findByMemberIdAndPostId(memberId, postId).isPresent();
    }

    @Query("SELECT pl FROM PostLike pl WHERE pl.member.id = :memberId AND pl.post.id = :postId")
    Optional<PostLike> findByMemberIdAndPostId(
            @Param("memberId") Long memberId,
            @Param("postId") Long postId
    );
}
