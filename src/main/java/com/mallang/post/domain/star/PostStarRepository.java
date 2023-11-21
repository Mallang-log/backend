package com.mallang.post.domain.star;

import com.mallang.auth.domain.Member;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.NotFoundPostStarException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostStarRepository extends JpaRepository<PostStar, Long> {

    boolean existsByPostAndMember(Post post, Member member);

    default PostStar getByPostIdAndMemberId(Long postId, Long memberId) {
        return findByPostIdAndMemberId(postId, memberId)
                .orElseThrow(NotFoundPostStarException::new);
    }

    Optional<PostStar> findByPostIdAndMemberId(Long postId, Long memberId);

    @Modifying
    @Query("DELETE FROM PostStar ps WHERE ps.post.id = :postId")
    void deleteAllByPostId(@Param("postId") Long postId);
}
