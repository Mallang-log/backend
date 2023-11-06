package com.mallang.post.query.dao.support;

import com.mallang.blog.domain.BlogName;
import com.mallang.post.domain.PostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeQuerySupport extends JpaRepository<PostLike, Long> {

    default boolean existsByMemberIdAndBlogNameAndPostId(
            Long memberId,
            BlogName blogName,
            Long postId
    ) {
        // TODO 이거 VS findTop1ByMemberIdAndBlogNameAndPostId 이거랑 성능차이 있나 확인하기
        return findByMemberIdAndBlogNameAndPostId(memberId, blogName, postId).isPresent();
    }

    @Query("SELECT pl FROM PostLike pl WHERE pl.member.id = :memberId AND pl.post.blog.name = :blogName AND pl.post.id = :postId")
    Optional<PostLike> findByMemberIdAndBlogNameAndPostId(
            @Param("memberId") Long memberId,
            @Param("blogName") BlogName blogName,
            @Param("postId") Long postId
    );
}
