package com.mallang.post.domain.like;

import com.mallang.auth.domain.Member;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.NotFoundPostLikeException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostAndMember(Post post, Member member);

    default PostLike getByPostIdAndMemberId(Long postId, Long memberId) {
        return findByPostIdAndMemberId(postId, memberId)
                .orElseThrow(NotFoundPostLikeException::new);
    }

    Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId);

    @Modifying
    @Query("DELETE FROM PostLike pl WHERE pl.post.id = :postId")
    void deleteAllByPostId(@Param("postId") Long postId);
}
