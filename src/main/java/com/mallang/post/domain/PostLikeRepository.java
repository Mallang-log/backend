package com.mallang.post.domain;

import com.mallang.member.domain.Member;
import com.mallang.post.exception.NotFoundPostLikeException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostAndMember(Post post, Member member);
    
    default PostLike getByPostIdAndMemberId(Long postId, Long memberId) {
        return findByPostIdAndMemberId(postId, memberId)
                .orElseThrow(NotFoundPostLikeException::new);
    }

    Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId);
}
