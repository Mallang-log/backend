package com.mallang.post.domain.star;

import com.mallang.auth.domain.Member;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostId;
import com.mallang.post.exception.NotFoundPostStarException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostStarRepository extends JpaRepository<PostStar, Long> {

    default PostStar getById(Long id) {
        return findById(id).orElseThrow(NotFoundPostStarException::new);
    }

    Optional<PostStar> findByPostAndMember(Post post, Member member);

    default PostStar getByPostAndMember(Long postId, String blogName, Long memberId) {
        return findByPostAndMember(postId, blogName, memberId)
                .orElseThrow(NotFoundPostStarException::new);
    }

    @Query("""
            SELECT ps FROM PostStar ps
            WHERE ps.post.id.postId = :postId
            AND ps.post.blog.name.value = :blogName
            AND ps.member.id = :memberId
            """)
    Optional<PostStar> findByPostAndMember(
            @Param("postId") Long postId,
            @Param("blogName") String blogName,
            @Param("memberId") Long memberId
    );

    List<PostStar> findAllByStarGroup(StarGroup starGroup);

    @Modifying
    @Query("DELETE FROM PostStar ps WHERE ps.post.id = :postId")
    void deleteAllByPostId(@Param("postId") PostId postId);
}
