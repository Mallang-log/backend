package com.mallang.post.domain.like;

import com.mallang.auth.domain.Member;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostId;
import com.mallang.post.exception.NotFoundPostLikeException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostAndMember(Post post, Member member);

    default PostLike getByPostAndMember(Long postId, String blogName, Long memberId) {
        return findByPostAndMember(postId, blogName, memberId)
                .orElseThrow(NotFoundPostLikeException::new);
    }

    @Query("""
            SELECT pl FROM PostLike pl
            WHERE pl.post.id.postId = :postId
            AND pl.post.blog.name.value = :blogName
            AND pl.member.id = :memberId
            """)
    Optional<PostLike> findByPostAndMember
            (@Param("postId") Long postId,
             @Param("blogName") String blogName,
             @Param("memberId") Long memberId
            );

    @Modifying
    @Query("DELETE FROM PostLike pl WHERE pl.post.id = :postId")
    void deleteAllByPostId(@Param("postId") PostId postId);
}
